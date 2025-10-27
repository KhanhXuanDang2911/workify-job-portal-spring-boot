package beworkify.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("keyGenerator")
public class KeyGenerator {
    public static String buildKeyWithPaginationSortsKeyword(int pageNumber, int pageSize,
                                                            List<String> sorts, String keyword, String whiteListFields){
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("pn:").append(pageNumber).append(":ps:").append(pageSize);
        if(keyword != null){
            keyBuilder.append(":").append(keyword);
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
}
