package Pr2;

import java.util.*;
import java.util.concurrent.*;

public class MultiplyArrayWithThreads {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть мінімальне значення діапазону: ");
        int min = scanner.nextInt();

        System.out.print("Введіть максимальне значення діапазону: ");
        int max = scanner.nextInt();

        System.out.print("Введіть множник: ");
        int multiplier = scanner.nextInt();

        // генерація масиву
        int size = 40 + new Random().nextInt(21);  // кіл-сть елементів від 40 до 60
        int[] numbers = generateRandomArray(min, max, size);

        System.out.println("Початковий масив (" + numbers.length + " елементів):");
        System.out.println(Arrays.toString(numbers));

        // для збереження результатів
        List<Integer> resultList = new CopyOnWriteArrayList<>();

        // для запуску потоків
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<Future<List<Integer>>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        //розмір частини масиву для кожного потоку
        int chunkSize = (int) Math.ceil((double) numbers.length / 4);

        for (int i = 0; i < 4; i++) {
            final int start = i * chunkSize;
            final int end = Math.min((i + 1) * chunkSize, numbers.length);

            // завдання для потоку
            Callable<List<Integer>> task = () -> {
                List<Integer> localResult = new ArrayList<>();
                for (int j = start; j < end; j++) {
                    localResult.add(numbers[j] * multiplier);
                }
                return localResult;
            };

            // додавання задачі до списку Future
            futures.add(executorService.submit(task));
        }

        for (Future<List<Integer>> future : futures) {
            while (!future.isDone()) {
                // Чекаємо завершення потоку
                Thread.sleep(2);  // Затримка для зменшення навантаження на процесор
            }

            try {
                // Додаємо результат після завершення завдання
                resultList.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (future.isCancelled()) {
                System.out.println("Задача була скасована");
            }
        }


        long endTime = System.currentTimeMillis();

        // Виведення обробленого масиву та його кількості елементів
        System.out.println("Оброблений масив (" + resultList.size() + " елементів):");
        System.out.println(resultList);

        // Виведення часу виконання
        System.out.println("Час виконання: " + (endTime - startTime) + " мс");

        // Завершення роботи executorService
        executorService.shutdown();
    }

    // для генерації масива
    public static int[] generateRandomArray(int min, int max, int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt((max - min) + 1) + min;
        }
        return array;
    }
}
