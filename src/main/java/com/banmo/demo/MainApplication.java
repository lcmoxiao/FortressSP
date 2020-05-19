package com.banmo.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        //关闭热部署
        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication.run(MainApplication.class, args);


        //  getDistance(7, 2, 100, List.of(2, 5, 13, 18, 19, 36, 88));
        //maxHeap(new int[]{1, 2, 3, 4, 5, 6, 7});

    }

    public static int getDistance(int n, int m, int target, List<Integer> d) {
        if (m == 0) return target;
        int[] distance = new int[n];
        for (int i = 0; i < n - 1; i++) {
            distance[i] = d.get(i + 1) - d.get(i);
        }
        distance[n - 1] = target - d.get(n - 1);

        //从大到小排序
        int[] mind = new int[m + 1];
        for (int i = 0; i < m + 1; i++) {
            mind[i] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < n; i++) {
            insert(distance[i], mind);
        }
        System.out.println(Arrays.toString(mind));
        return mind[0];
    }

    static void insert(int n, int[] a) {
        if (n > a[0]) return;
        for (int i = 1; i < a.length; i++) {
            a[i - 1] = a[i];
            if (a[i] < n) {
                a[i - 1] = n;
                return;
            }
        }
        a[a.length - 1] = n;
    }

    static void maxHeap(int[] A) {
        if (A.length <= 1) return;
        for (int i = 1; i < A.length; i++) {
            int m = i;
            while (m > 0) {
                if (A[m] > A[(m + 1) / 2 - 1]) {
                    int tmp = A[m];
                    A[m] = A[(m + 1) / 2 - 1];
                    A[(m + 1) / 2 - 1] = tmp;
                    m = (m + 1) / 2 - 1;
                } else {
                    break;
                }
            }
        }
        String str = "abcdef";
        str.length();
        System.out.println(Arrays.toString(A));
    }


}
