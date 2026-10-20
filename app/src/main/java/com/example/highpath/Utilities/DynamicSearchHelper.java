package com.example.highpath.Utilities;

import java.util.ArrayList;
import java.util.List;

public class DynamicSearchHelper<T> {

    // Interface to allow different classes to define what field they want to search
    public interface SearchProperty<T> {
        String getSearchableString(T item);
    }

    public List<T> filter(List<T> fullList, String query, SearchProperty<T> property) {
        List<T> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            return new ArrayList<>(fullList); // Return full list if search is empty
        }

        String lowerCaseQuery = query.toLowerCase().trim();

        for (T item : fullList) {
            String searchableValue = property.getSearchableString(item).toLowerCase();
            if (searchableValue.contains(lowerCaseQuery)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }
}