package com.example.bankcards.core.dto;


import java.util.List;

public record PageDto<T>(
        List<T> content,
        Integer page,
        Integer size,
        Integer totalPages,
        Boolean hasNext,
        Boolean hasLast
) {
}
