package com.codeleap.xilab.api.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.codeleap.xilab.api.models.StorageImageData;
import com.codeleap.xilab.api.models.StorageImageInfo;
import com.codeleap.xilab.api.repository.UserAvatarRepository;
import com.codeleap.xilab.api.repository.UserRepository;

import org.imgscalr.Scalr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageService {

	public static final Dimension IMG_MAX_SIZE = new Dimension(1500, 1500);
	public static final Dimension IMG_THUMBNAIL_SIZE = new Dimension(150, 150);

	@Value("${xilab.app.s3AccessKey}")
	private String s3AccessKey;

	@Value("${xilab.app.s3SecretKey}")
	private String s3SecretKey;

	@Value("${xilab.app.s3Bucket}")
	private String s3Bucket;

	@Value("${xilab.app.s3BucketRegion}")
	private String s3BucketRegion;

	@Value("${xilab.app.resourceUrl}")
	private String resourceUrl;

	@Autowired
    UserAvatarRepository userAvatarRepository;

	public StorageImageData processAvatarImage(byte[] byteArray, Long userId) {
        try {
            if (byteArray.length > 10 * 1024 * 1024) {
                throw new RuntimeException("File size is to large");
            }

            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(byteArray));
            Dimension expectedSize = getScaledDimension(new Dimension(inputImage.getWidth(), inputImage.getHeight()),
                    IMG_MAX_SIZE);
            Dimension thumbnailCropSize = getScaledDimension(
                    new Dimension(inputImage.getWidth(), inputImage.getHeight()), IMG_THUMBNAIL_SIZE);

            byte[] expectedByteArray = resize(inputImage, expectedSize);
            byte[] thumbnailByteArray = resize(inputImage, thumbnailCropSize);

            return new StorageImageData()
                    .setThumbnailImage(thumbnailByteArray)
                    .setOriginalImage(expectedByteArray);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot process the base64 image. " + ex.getMessage());
        }
    }

    private boolean saveToLocal(byte[] mainBytes, byte[] thumbnailBytes, String key) {
        try {
            String mainFileName = String.format("/%s-main.jpg", key);
            String thumbnailFileName = String.format("/%s-thumbnail.jpg", key);

            Path source = Paths.get(this.getClass().getResource("/").getPath());
            Path root = Paths.get(source.toAbsolutePath() + "/uploads/avatars/");

            deleteLocalAvatarImage(key);
            saveFile(root, mainFileName, mainBytes);
            saveFile(root, thumbnailFileName, thumbnailBytes);

            return true;
        }
        catch (Exception ex) {
            throw new RuntimeException("Upload images to S3 failed with message: " + ex.getMessage());
        }
    }

    private boolean saveFile(Path folder, String fileName, byte[] fileContent) {
        File file = new File(folder.toString() + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileContent);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean deleteLocalAvatarImage(String userId) {
        try {
            Path source = Paths.get(this.getClass().getResource("/").getPath());
            Path root = Paths.get(source.toAbsolutePath() + "/uploads/avatars/");

            String mainFileName = String.format("/%s-main.jpg", userId);
            String thumbnailFileName = String.format("/%s-thumbnail.jpg", userId);
            Path mainFilePath = Paths.get(root.toAbsolutePath() + mainFileName);
            Path thumbnailFilePath = Paths.get(root.toAbsolutePath() + thumbnailFileName);

            if(Files.exists(mainFilePath)){
                Files.delete(mainFilePath);
            }
            if(Files.exists(thumbnailFilePath)){
                Files.delete(thumbnailFilePath);
            }
            return true;
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot delete S3 images with message: " + ex.getMessage());
        }
    }

    public boolean deleteAvatarImage(Long userId) {
        try {
            String mainFileName = String.format("xilab/%s-main.jpg", userId.toString());
            String thumbnailFileName = String.format("xilab/%s-thumbnail.jpg", userId.toString());

            AmazonS3 s3 = gets3Client();
            if (s3.doesObjectExist(s3Bucket, mainFileName)) {
                s3.deleteObject(s3Bucket, mainFileName);
            }
            if (s3.doesObjectExist(s3Bucket, thumbnailFileName)) {
                s3.deleteObject(s3Bucket, thumbnailFileName);
            }
            return true;
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot delete S3 images with message: " + ex.getMessage());
        }
    }

    public byte[] loadImageFromDB(String filename) {
        try {
            var imageParts = filename.split("\\.")[0].split("-");
            var userId = Long.parseLong(imageParts[0]);
            var isMain = imageParts[1].equalsIgnoreCase("main");
            var userAvatarOtp = userAvatarRepository.findByUserId(userId);
            if(!userAvatarOtp.isPresent()){
                return null;
            }
            return isMain ? userAvatarOtp.get().getMainAvatar() : userAvatarOtp.get().getThumbAvatar();
        } catch (Exception e) {
            throw new RuntimeException("Cannot read image from database");
        }
    }

    public Resource loadImageFile(String filename) {
        try {
            Path source = Paths.get(this.getClass().getResource("/").getPath());
            Path root = Paths.get(source.toAbsolutePath() + "/uploads/avatars/");
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot load");
        }
    }

	public StorageImageInfo getLocalAvatarImage(Long userId) {
		String mainFileUrl = String.format("%s/api/stream/avatar/%s-main.jpg",resourceUrl, userId.toString());
		String thumbnailFileUrl = String.format("%s/api/stream/avatar/%s-thumbnail.jpg",resourceUrl, userId.toString());

		return new StorageImageInfo().setOriginalImageUrl(mainFileUrl).setThumbnailImageUrl(thumbnailFileUrl);
	}

	public StorageImageInfo getAvatarImage(Long userId) {
		AmazonS3 s3 = gets3Client();
		String mainFileName = String.format("xilab/%s-main.jpg", userId.toString());
		String thumbnailFileName = String.format("xilab/%s-thumbnail.jpg", userId.toString());

		Date validDate = new DateTime().plusDays(2).toDate();
		String mainUrl = s3.generatePresignedUrl(s3Bucket, mainFileName, validDate).toString();
		String thumbnailUrl = s3.generatePresignedUrl(s3Bucket, thumbnailFileName, validDate).toString();
		return new StorageImageInfo().setOriginalImageUrl(mainUrl).setThumbnailImageUrl(thumbnailUrl);
	}

	private AmazonS3 gets3Client() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withRegion(Regions.fromName(s3BucketRegion)).build();
	}

	private boolean uploadToS3(byte[] mainBytes, byte[] thumbnailBytes, String key) {
		try {
			String mainFileName = String.format("xilab/%s-main.jpg", key);
			String thumbnailFileName = String.format("xilab/%s-thumbnail.jpg", key);

			AmazonS3 s3 = gets3Client();
			uploadToS3(s3, mainBytes, mainFileName);
			uploadToS3(s3, thumbnailBytes, thumbnailFileName);

			return true;
		}
		catch (Exception ex) {
			throw new RuntimeException("Upload images to S3 failed with message: " + ex.getMessage());
		}
	}

	private void uploadToS3(AmazonS3 s3, byte[] fileBytes, String fileName) {
		try {
			InputStream fileStream = new ByteArrayInputStream(fileBytes);
			if (s3.doesObjectExist(s3Bucket, fileName)) {
				s3.deleteObject(s3Bucket, fileName);
			}

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(fileBytes.length);
			metadata.setContentType("image/jpeg");
			metadata.setCacheControl("public, max-age=31536000");
			s3.putObject(s3Bucket, fileName, fileStream, metadata);
		}
		catch (Exception ex) {
			throw new RuntimeException("Upload images to S3 failed with message: " + ex.getMessage());
		}
	}

	private byte[] resize(BufferedImage image, Dimension expectedSize) {
        try {
            BufferedImage resizeImage = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC,
                    expectedSize.width, expectedSize.height, Scalr.OP_ANTIALIAS);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BufferedImage backgroundImage = new BufferedImage(resizeImage.getWidth(), resizeImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            backgroundImage.createGraphics().drawImage(resizeImage, 0, 0, Color.WHITE, null);

            ImageIO.write(backgroundImage, "jpg", outputStream);
            return outputStream.toByteArray();
        }
		catch (Exception ex) {
			return null;
		}
	}

	private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
		int original_width = imgSize.width;
		int original_height = imgSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;
		int new_width = original_width;
		int new_height = original_height;

		if (original_width > bound_width) {
			new_width = bound_width;
			new_height = (new_width * original_height) / original_width;
		}

		if (new_height > bound_height) {
			new_height = bound_height;
			new_width = (new_height * original_width) / original_height;
		}

		return new Dimension(new_width, new_height);
	}
}
