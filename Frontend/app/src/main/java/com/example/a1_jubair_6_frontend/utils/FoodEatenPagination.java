package com.example.a1_jubair_6_frontend.utils;

import com.example.a1_jubair_6_frontend.models.FoodEaten;

import java.util.ArrayList;
import java.util.List;

public class FoodEatenPagination {
    private static final int PAGE_SIZE = 5; // Number of items per page
    private List<FoodEaten> allItems;
    private int currentPage = 0;
    private int totalPages = 0;

    public FoodEatenPagination() {
        this.allItems = new ArrayList<>();
    }

    public void setItems(List<FoodEaten> items) {
        this.allItems = new ArrayList<>(items);
        this.totalPages = (int) Math.ceil((double) items.size() / PAGE_SIZE);
        this.currentPage = 0;
    }

    public List<FoodEaten> getCurrentPageItems() {
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allItems.size());

        if (start >= allItems.size()) {
            return new ArrayList<>();
        }

        return allItems.subList(start, end);
    }

    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
        }
    }

    public int getCurrentPage() {
        return currentPage + 1;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
