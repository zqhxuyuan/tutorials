package org.tguduru.guava.collections;

import com.google.common.collect.HashBasedTable;

import java.util.Map;

/**
 * Table holds key and values just like a Table which has a column , row and value in the intersection of those. Its
 * useful for matrix representation. Its a 3 dimensional array with easy to understand API.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/19/15
 */
public class TableDemo {
    public static void main(final String[] args) {
        // row,column,value based Table
        final HashBasedTable<Integer, Integer, String> hashBasedTable = HashBasedTable.create();
        hashBasedTable.put(1, 1, "Row1Column1Value");
        hashBasedTable.put(1, 2, "Row1Column2Value");
        hashBasedTable.put(2, 1, "Row2Column1Value");
        hashBasedTable.put(2, 2, "Row2Column2Value");
        hashBasedTable.put(2, 3, "Row2Column3Value"); // see now we added a 3rd column for the table, it means the other
                                                      // rows will have null values for 3rd column.
        System.out.println(hashBasedTable.get(2, 1)); // Row2Column1Value
        System.out.println(hashBasedTable.get(1, 3)); // returns null

        // Table Operations
        final Map<Integer, String> rowMap = hashBasedTable.column(1); // this returns map of first column with key as
                                                                      // row and
        // row value as value.
        System.out.println(rowMap);

        final Map<Integer, String> columnMap = hashBasedTable.row(2);// this return a map with 2nd row, key = column ,
                                                                     // value =
        // column value.
        System.out.println(columnMap);

        // Like these there are lot of methods for different use cases. And also lot of implementations for Table
        // interface as well.
    }
}
