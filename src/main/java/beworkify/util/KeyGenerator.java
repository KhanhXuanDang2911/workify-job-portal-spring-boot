package beworkify.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("keyGenerator")
public class KeyGenerator {
    public static String buildKeyWithPaginationSortsKeyword(int pageNumber, int pageSize,
                                                            List<String> sorts, String keyword, List<String> whiteListFields){
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("pn:").append(pageNumber).append(":ps:").append(pageSize);
        if (keyword != null && !keyword.isBlank()) {
            keyBuilder.append(":kw:").append(keyword.trim());
        }
        if(sorts != null && !sorts.isEmpty()){
            sorts = sorts.stream().filter(sort -> {
                boolean isValid = AppUtils.isValidSortParam(sort);
                if (!isValid)
                    return false;
                String sortField = sort.split(":")[0];
                return whiteListFields.contains(sortField);
            }).collect(Collectors.toList());

            sorts.sort(String::compareTo);

            sorts.forEach(sort -> {
                String sortField = sort.split(":")[0];
                String sortOrder = sort.split(":")[1];
                keyBuilder.append(":").append(sortField).append("-").append(sortOrder);
            });
        }

        return keyBuilder.toString();
    }

    public static String buildKeyWithPaginationSortsKeywordForIndustries(int pageNumber, int pageSize,
                                                                   List<String> sorts, String keyword, List<String> whiteListFields, Long categoryId) {
        String firstResult = buildKeyWithPaginationSortsKeyword(pageNumber, pageSize, sorts, keyword, whiteListFields);
        StringBuilder resultKeyBuilder = new StringBuilder(firstResult);
        if(categoryId != null){
            resultKeyBuilder.append(":c:").append(categoryId);
        }
        return resultKeyBuilder.toString();
    }

    public static String buildKeyWithPaginationSortsKeywordForPost(int pageNumber, int pageSize,
                                                          List<String> sorts, String keyword, List<String> whiteListFields, Long categoryId, boolean isPublic, Long authorId) {
        String firstResult = buildKeyWithPaginationSortsKeyword(pageNumber, pageSize, sorts, keyword, whiteListFields);
        StringBuilder resultKeyBuilder = new StringBuilder(firstResult);
        if(categoryId != null){
            resultKeyBuilder.append(":c:").append(categoryId);
        }
        if(authorId != null){
            resultKeyBuilder.append(":a:").append(authorId);
        }
        resultKeyBuilder.append(":").append(isPublic);
        return resultKeyBuilder.toString();
    }

    public static String buildKeyForHiringJobs(Long employerId, int pageNumber, int pageSize,
                                               List<String> sorts, List<String> whiteListFields) {
        String firstResult = buildKeyWithPaginationSortsKeyword(pageNumber, pageSize, sorts, null, whiteListFields);
        StringBuilder resultKeyBuilder = new StringBuilder(firstResult);
        if(employerId != null){
            resultKeyBuilder.append(":e:").append(employerId);
        }
        return resultKeyBuilder.toString();
    }

    public static String buildKeyForGetAllJobs(int pageNumber, int pageSize, Long industryId,
                                               Long provinceId, List<String> sorts, String keyword, List<String> whiteListFields) {
        String firstResult = buildKeyWithPaginationSortsKeyword(pageNumber, pageSize, sorts, keyword, whiteListFields);
        StringBuilder resultKeyBuilder = new StringBuilder(firstResult);
        if(industryId != null){
            resultKeyBuilder.append(":i:").append(industryId);
        }
        if(provinceId != null){
            resultKeyBuilder.append(":p:").append(provinceId);
        }
        return resultKeyBuilder.toString();
    }
}
