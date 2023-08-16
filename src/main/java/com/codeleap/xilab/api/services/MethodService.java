package com.codeleap.xilab.api.services;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codeleap.xilab.api.models.StorageImageInfo;
import com.codeleap.xilab.api.models.csv.MethodCsvRecord;
import com.codeleap.xilab.api.models.entities.BadgeType;
import com.codeleap.xilab.api.models.entities.Comment;
import com.codeleap.xilab.api.models.entities.Method;
import com.codeleap.xilab.api.models.entities.MethodSet;
import com.codeleap.xilab.api.models.entities.QComment;
import com.codeleap.xilab.api.models.entities.QMethod;
import com.codeleap.xilab.api.models.entities.QRating;
import com.codeleap.xilab.api.models.entities.Rating;
import com.codeleap.xilab.api.models.entities.UserBadge;
import com.codeleap.xilab.api.models.entities.auth.User;
import com.codeleap.xilab.api.payload.request.CommentMethodRequest;
import com.codeleap.xilab.api.payload.request.CreateMethodRequest;
import com.codeleap.xilab.api.payload.request.RateMethodRequest;
import com.codeleap.xilab.api.payload.response.BaseResponse;
import com.codeleap.xilab.api.payload.response.Pagination;
import com.codeleap.xilab.api.payload.response.method.CommentItemResponse;
import com.codeleap.xilab.api.payload.response.method.MethodResponse;
import com.codeleap.xilab.api.payload.response.method.RatingItemResponse;
import com.codeleap.xilab.api.repository.CommentRepository;
import com.codeleap.xilab.api.repository.MethodRepository;
import com.codeleap.xilab.api.repository.MethodSetRepository;
import com.codeleap.xilab.api.repository.RatingRepository;
import com.codeleap.xilab.api.repository.UserBadgeRepository;
import com.codeleap.xilab.api.repository.UserRepository;
import com.codeleap.xilab.api.utils.CollectionUtils;
import com.codeleap.xilab.api.utils.DataUtils;
import com.codeleap.xilab.api.utils.StringUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MethodService implements InitializingBean {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    MethodSetRepository methodSetRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    UserBadgeRepository userBadgeRepository;

    @Autowired
    ImageService imageService;

    @Autowired
    UserService userService;

    JPAQueryFactory jpaQueryFactory;

    private static final Integer HIGH_RATING_COUNT = 20;
    private static final Double HIGH_RATING_SCORE = 8.5;

    @Override
    public void afterPropertiesSet() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public ResponseEntity<?> methodSearch(Long userId, String keyword, Short certainPhase, Pageable pageable, Boolean includeMethods, Boolean includeMethodSets) {
        try {
            var $a = QMethod.method;
            BooleanBuilder whereClause = new BooleanBuilder();
            if(!StringUtils.isNullOrWhiteSpace(keyword)){
                whereClause.and($a.name.containsIgnoreCase(keyword.trim())
                        .or($a.createdByUser.firstName.containsIgnoreCase(keyword.trim()))
                        .or($a.createdByUser.lastName.containsIgnoreCase(keyword.trim()))
                        .or($a.input.containsIgnoreCase(keyword.trim()))
                        .or($a.relevantPhases.containsIgnoreCase(keyword.trim()))
                        .or($a.output.containsIgnoreCase(keyword.trim()))
                        .or($a.description.containsIgnoreCase(keyword.trim()))
                                .or($a.descriptionBrief.containsIgnoreCase(keyword.trim())));
            }

            if(!DataUtils.isTrue(includeMethods)){
                if(DataUtils.isTrue(includeMethodSets)){
                    whereClause.and($a.isSet.eq(true));
                }else{
                    whereClause.and($a.isSet.isNull());
                }
            }else{
                if(!DataUtils.isTrue(includeMethodSets)){
                    whereClause.and($a.isSet.eq(false));
                }
            }
            if(userId == 0l){
                whereClause.and($a.isPublished.eq(true));
            }else{
                var userOpt = userRepository.findById(userId);
                if(!userOpt.isPresent()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("User not found"));
                }
                whereClause.and(($a.createdByUser.id.eq(userId)).or($a.isPublished.eq(true)));
            }

            PathBuilder<Method> entityPath = new PathBuilder<>(Method.class, "method");
            OrderSpecifier orderSpecifier = ($a.certainPhase.subtract(certainPhase)).abs().asc();

            for (Sort.Order order : pageable.getSort()) {
                if(order.getProperty().equals("phase")){
                    if(order.getDirection().name().equalsIgnoreCase("DESC")){
                        orderSpecifier = ($a.certainPhase.subtract(certainPhase)).abs().desc();
                    }
                    break;
                }
                PathBuilder<Object> path = entityPath.get(order.getProperty());
                orderSpecifier = new OrderSpecifier(com.querydsl.core.types.Order.valueOf(order.getDirection().name()), path);
                break;
            }

            Long totalItems = jpaQueryFactory.from($a)
                    .select($a.id)
                    .where(whereClause)
                    .orderBy(orderSpecifier)
                    .fetchCount();

            List<Method> searchResult = jpaQueryFactory.from($a)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .select($a)
                    .where(whereClause)
                    .orderBy(orderSpecifier)
                    .fetch();

            var responseData = searchResult.stream()
                    .map(x -> new MethodResponse(x, userId, true))
                    .collect(Collectors.toList());

            var page = new Pagination()
                    .setCurrentPage(pageable.getPageNumber())
                    .setItemsPerPage(pageable.getPageSize())
                    .setTotalItems(totalItems);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse().setPagination(page).setData(responseData));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in searching for methods"));
        }
    }

    public ResponseEntity<?> createOrUpdateMethod(CreateMethodRequest createMethodRequest, Long userId, Long methodId) {
        try {
            if(userId == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid user token"));
            }
            var userOpt = userRepository.findById(userId);
            if(!userOpt.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("User not found"));
            }

            Method methodInfo;
            HttpStatus responseStatus = HttpStatus.OK;
            String responseMessage = "Method created";
            if(methodId != null){
                var methodOpt = methodRepository.findById(methodId);
                if(!methodOpt.isPresent()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new BaseResponse("Method not found with given Id: " + methodId));
                }
                methodInfo = methodOpt.get();
                if(!methodInfo.getCreatedByUser().getId().equals(userId)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new BaseResponse("You cannot update another one's method"));
                }
                createMethodRequest.updateDBEntity(methodInfo);
                if(DataUtils.isTrue(createMethodRequest.getIsMethodSet())){
                    methodSetRepository.deleteBySetId(methodId);
                }
                responseMessage = "Method updated";
            }else{
                responseStatus = HttpStatus.CREATED;
                methodInfo = createMethodRequest.toDBEntity();
            }

            methodInfo.setCreatedByUser(userOpt.get());
            var saveResult = methodRepository.save(methodInfo);

            if(DataUtils.isTrue(createMethodRequest.getIsMethodSet())){
                if(CollectionUtils.hasItems(createMethodRequest.getUsedMethodIds())){
                    Short idx = 0;
                    for(Long id : createMethodRequest.getUsedMethodIds()){
                        log.info("MethodSet: " + id);
                        var setItem = new MethodSet()
                                .setItemId(id)
                                .setItemOrder(idx)
                                .setSetId(saveResult.getId());
                        idx++;
                        methodSetRepository.save(setItem);
                    }
                }
            }

            if(createMethodRequest.getIsPublished()) {
                checkAndGrantBadgeType(methodInfo.getCreatedByUser(), BadgeType.METHOD_CREATOR);
            }
            return ResponseEntity.status(responseStatus).body(new BaseResponse(responseMessage));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in updating method"));
        }
    }

    public ResponseEntity<?> getMethodInfo(Long methodId, long userId) {
        try {
            Method methodInfo;
            HttpStatus responseStatus = HttpStatus.OK;
            var methodOpt = methodRepository.findById(methodId);
            if(!methodOpt.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse("Method not found with given Id: " + methodId));
            }
            methodInfo = methodOpt.get();

            return ResponseEntity.status(responseStatus).body(new BaseResponse(new MethodResponse(methodInfo, userId, false)));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in getting method data"));
        }
    }

    public ResponseEntity<?> addMethodRating(Long userId, RateMethodRequest request, Long methodId) {
        try {
            var user = userRepository.findById(userId);
            var method = methodRepository.findById(methodId);
            if(!method.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Method not found"));
            }

            if(!user.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("User not found"));
            }

            if(user.get().getId().equals(method.get().getCreatedByUser().getId())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("You cannot rate your own method"));
            }

            var rating = new Rating()
                    .setMethodId(methodId)
                    .setMessage(request.getMessage())
                    .setHeadline(request.getHeadline())
                    .setScore(request.getStars())
                    .setRatedByUser(user.get())
                    .setRatedOn(LocalDateTime.now());

            rating = ratingRepository.save(rating);

            if(rating != null) {
                checkAndGrantBadgeType(user.get(), BadgeType.METHOD_FACILITATOR);
            }
            // Calculate averageRating for current method
            var methodRatings = ratingRepository.getByMethodId(methodId);
            var count = 1;
            Double averageRating;
            if(!CollectionUtils.isNullOrNoItem(methodRatings)){
                averageRating = methodRatings.stream().mapToInt(x -> x.getScore()).average().getAsDouble();
                averageRating = Math.round(averageRating * 10.0) / 10.0;
                count = methodRatings.size();
            }else{
                averageRating = request.getStars() * 1.0;
            }
            method.get().setAverageRating(averageRating);
            methodRepository.save(method.get());

            // Check and set Badge
            if(count >= HIGH_RATING_COUNT && averageRating >= HIGH_RATING_SCORE) {
                checkAndGrantBadgeType(method.get().getCreatedByUser(), BadgeType.BEST_METHOD_CREATOR);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BaseResponse(new RatingItemResponse(rating))
                            .addExtraData("averageRating",averageRating)
                            .addExtraData("totalRateItems", count));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in adding comment to method"));
        }
    }

    public ResponseEntity<?> addMethodComment(Long userId, CommentMethodRequest request, Long methodId) {
        try {
            var userOtp = userRepository.findById(userId);
            if(!userOtp.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("User not found"));
            }

            var user = userOtp.get();
            var comment = new Comment()
                    .setMethodId(methodId)
                    .setMessage(request.getMessage())
                    .setCommentedByUser(user)
                    .setCommentedOn(LocalDateTime.now());

            if(request.getParentCommentId() != null){
                var parentCommentOpt = commentRepository.findById(request.getParentCommentId());
                if(parentCommentOpt.isPresent()){
                    comment.setParentComment(parentCommentOpt.get());
                }
            }

            comment = commentRepository.save(comment);
            if(comment != null) {
                checkAndGrantBadgeType(user, BadgeType.METHOD_FACILITATOR);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse(new CommentItemResponse(comment)));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in adding comment to method"));
        }
    }

    public ResponseEntity<?> getAllCommentsOfMethod(Long methodId) {
        try {
            var $a = QComment.comment;
            BooleanBuilder whereClause = new BooleanBuilder()
                    .and($a.methodId.eq(methodId))
                    .and($a.parentComment.isNull());

            OrderSpecifier orderSpecifier = $a.commentedOn.desc();

            var count = jpaQueryFactory.from($a)
                    .where($a.methodId.eq(methodId))
                    .fetchCount();

            List<Comment> searchResult = jpaQueryFactory.from($a)
                    .select($a)
                    .where(whereClause)
                    .orderBy(orderSpecifier)
                    .fetch();

            var responseData = searchResult.stream()
                    .map(x -> buildCommentResponseItem(x))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse()
                            .setData(responseData)
                            .addExtraData("totalCommentItems", count));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in searching for methods"));
        }
    }

    private CommentItemResponse buildCommentResponseItem(Comment comment){
        var responseItem = new CommentItemResponse(comment);
        if(comment.getCommentedByUser().getHasAvatar()) {
            StorageImageInfo imageInfo = imageService.getLocalAvatarImage(comment.getCommentedByUser().getId());
            responseItem.setCommenterAvatarUrl(imageInfo.getThumbnailImageUrl());
        }
        return responseItem;
    }

    public ResponseEntity<?> getAllRatingsOfMethod(Long methodId) {
        try {
            var $a = QRating.rating;
            BooleanBuilder whereClause = new BooleanBuilder()
                    .and($a.methodId.eq(methodId));

            OrderSpecifier orderSpecifier = $a.ratedOn.desc();

            List<Rating> searchResult = jpaQueryFactory.from($a)
                    .select($a)
                    .where(whereClause)
                    .orderBy(orderSpecifier)
                    .fetch();


            var responseData = searchResult.stream()
                    .map(x -> buildRatingResponseItem(x))
                    .collect(Collectors.toList());

            log.info("RatingItem count: " + responseData);
            var count = 0;
            Double averageRating;
            if(!CollectionUtils.isNullOrNoItem(responseData)){
                averageRating = responseData.stream().mapToInt(x -> x.getScore()).average().getAsDouble();
                averageRating = Math.round(averageRating * 10.0) / 10.0;
                count = responseData.size();
            }else{
                averageRating = 0d;
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse().setData(responseData)
                            .addExtraData("averageRating",averageRating)
                            .addExtraData("totalRateItems", count));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in searching for methods", e.getMessage()));
        }
    }

    private RatingItemResponse buildRatingResponseItem(Rating ratingItem){
        log.info("RatingItem: " + ratingItem);
        var responseItem = new RatingItemResponse(ratingItem);
        // if(ratingItem.getRatedByUser().getHasAvatar()) {
        //     StorageImageInfo imageInfo = imageService.getLocalAvatarImage(ratingItem.getRatedByUser().getId());
        //     responseItem.setRaterAvatarUrl(imageInfo.getThumbnailImageUrl());
        // }
        return responseItem;
    }

    public ResponseEntity<?> deleteMethod(Long userId, Long methodId) {
        try {
            Method methodInfo;
            var methodOpt = methodRepository.findById(methodId);
            if (!methodOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse("Method not found with given Id: " + methodId));
            }
            methodInfo = methodOpt.get();
            if (!methodInfo.getCreatedByUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse("You cannot delete another one's method"));
            }

            if(methodInfo.getIsSet()){
                methodSetRepository.deleteBySetId(methodId);
            }

            commentRepository.deleteByMethodId(methodId);
            ratingRepository.deleteByMethodId(methodId);

            methodRepository.deleteById(methodId);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Method deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in deleting method"));
        }
    }

    public ResponseEntity<?> publishMethod(Long userId, Long methodId) {
        try {
            Method methodInfo;
            var methodOpt = methodRepository.findById(methodId);
            if (!methodOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse("Method not found with given Id: " + methodId));
            }
            methodInfo = methodOpt.get();
            if (!methodInfo.getCreatedByUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse("You cannot publish another one's method"));
            }

            if(DataUtils.isTrue(methodInfo.getIsPublished())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse("This method is already published"));
            }

            methodInfo.setIsPublished(true);
            methodRepository.save(methodInfo);

            // Check and set Badge
            checkAndGrantBadgeType(methodInfo.getCreatedByUser(), BadgeType.METHOD_CREATOR);

            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Method published"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in publishing method"));
        }
    }

    public List<MethodCsvRecord> readMethodCsvRecords(MultipartFile csvFile) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(csvFile.getBytes())));
            List<MethodCsvRecord> result = new CsvToBeanBuilder(reader)
                    .withSeparator(',')
                    .withType(MethodCsvRecord.class).build().parse();
            if(result!= null && result.size() > 0) {
                result.remove(0);
            }
            return result;
        } catch (Exception ex) {
            log.error("Reading MethodCsvRecord failed with error: " + ex.getMessage());
            return null;
        }
    }

    public ResponseEntity<?> importMethodsFromUploadCsvFile(Long userId, MultipartFile csvFile) {
        try {
            var methodCsvRecords = readMethodCsvRecords(csvFile);
            if (methodCsvRecords == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Cannot read CSV file"));
            }

            int validCount = 0;
            var importedDate = LocalDateTime.now();
            for (var record : methodCsvRecords) {
                if (!record.isValid())
                    continue;

                var method = record.convertToCreateMethodRequest();
                var user = userService.checkAndCreateUser(record.getOwnerEmail().trim(),
                        record.getOwnerFirstName().trim(),
                        record.getOwnerLastName().trim());
                if (user == null)
                    continue;
                method.setCreatedByUser(user);
                method.setCreatedOn(importedDate);
                method.setImportedBy(userId);
                methodRepository.save(method);
                validCount++;
            }

            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("%s method(s) found, %s method(s) imported", methodCsvRecords.size(), validCount));
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Unexpected error while updating avatar for User: " + userId));
        }
    }

    private void checkAndGrantBadgeType(User user, BadgeType badgeType){
        var badgeList = userBadgeRepository.getByBelongToUser(user);
        boolean addBadge = true;
        if (!CollectionUtils.isNullOrNoItem(badgeList)) {
            var check = badgeList.stream().anyMatch(x -> x.getBadgeType().equals(badgeType.toString()));
            if (check) {
                addBadge = false;
            }
        }
        if (addBadge) {
            var userBadge = new UserBadge()
                    .setBadgeType(badgeType.toString())
                    .setAchievedOn(LocalDateTime.now())
                    .setBelongToUser(user);
            userBadgeRepository.save(userBadge);
        }
    }
}
