package com.codeleap.xilab.api.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Pagination {
    private Integer currentPage;
    private Long totalItems;
    private Integer itemsPerPage;
}
