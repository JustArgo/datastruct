package SORT;

/**
 * @author 黄永宗
 */
public class QuickSort {

    public static void main(String[] args) {
        int[] array = {5, 1, 7, 3, 1, 6, 9, 4};

        quickSort(array, 0, array.length - 1);

        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.println("");
    }

    private static void quickSort(int[] array, int start, int end) {
        if (start >= end) {
            return;
        }

        int left = start;
        int right = end;
        int key = array[left];

        while (left < right) {
            while (right > left && array[right] >= key) {
                right--;
            }

            array[left] = array[right];

            while (left < right && array[left] <= key) {
                left++;
            }

            array[right] = array[left];
        }
        array[left] = key;
        quickSort(array, start, left - 1);
        quickSort(array, right + 1, end);
    }

}
