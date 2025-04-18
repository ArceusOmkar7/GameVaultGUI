package com.project.gamevaultcli.helpers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

public class Helper {

    public static <T> void printTable(List<T> data, List<String> columnNames) {
        if (data == null || data.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }

        if (columnNames == null || columnNames.isEmpty()) {
             System.out.println("Column names should be provided.");
             return;
        }

        // Determine maximum lengths for each column
        List<Integer> maxColumnLengths = new ArrayList<>();
        for (String columnName : columnNames) {
            maxColumnLengths.add(columnName.length());
        }

        // Calculate max lengths based on data
        for (T item : data) {
            for (int i = 0; i < columnNames.size(); i++) {
                try {
                    String fieldName = columnNames.get(i);
                    Field field = item.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true); // Allow access to private fields
                    Object value = field.get(item);
                    String stringValue = (value != null) ? value.toString() : "null";
                    maxColumnLengths.set(i, Math.max(maxColumnLengths.get(i), stringValue.length()));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.err.println("Error accessing field " + columnNames.get(i) + ": " + e.getMessage());
                    //In case field cannot be accessed, default it to the length of the columnName
                    maxColumnLengths.set(i, columnNames.get(i).length());
                }
            }
        }

        // Create format string
        StringBuilder formatBuilder = new StringBuilder();
        for (int maxLength : maxColumnLengths) {
            formatBuilder.append("| %-").append(maxLength).append("s ");
        }
        formatBuilder.append("|%n");
        String format = formatBuilder.toString();

        // Print header
        printSeparator(maxColumnLengths);
        System.out.printf(format, columnNames.toArray());
        printSeparator(maxColumnLengths);

        // Print data
        for (T item : data) {
            Object[] rowData = new Object[columnNames.size()];
            for (int i = 0; i < columnNames.size(); i++) {
                try {
                    String fieldName = columnNames.get(i);
                    Field field = item.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(item);
                    rowData[i] = (value != null) ? value.toString() : "null";
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.err.println("Error accessing field " + columnNames.get(i) + ": " + e.getMessage());
                    rowData[i] = "ERROR"; // Or some other error indicator
                }
            }
            System.out.printf(format, rowData);
        }

        // Print footer
        printSeparator(maxColumnLengths);
    }

    private static void printSeparator(List<Integer> columnLengths) {
        System.out.print("+");
        for (int length : columnLengths) {
            for (int i = 0; i < length + 2; i++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println();
    }
}