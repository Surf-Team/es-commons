package ru.es.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Threads
{
	public static void executeTasksInParallel(List<Runnable> tasks, int threadCount) {
		if (tasks == null || tasks.isEmpty()) {
			return;
		}

		if (threadCount <= 0) {
			throw new IllegalArgumentException("Количество потоков должно быть больше нуля");
		}

		ExecutorService executor = Executors.newFixedThreadPool(threadCount);

		try {
			// Отправляем все задачи на выполнение
			for (Runnable task : tasks) {
				executor.submit(task);
			}

			// Прекращаем принимать новые задачи
			executor.shutdown();

			// Ждем завершения всех задач
			try {
				// Ожидаем завершения в течение разумного времени
				if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		} finally {
			if (!executor.isTerminated()) {
				executor.shutdownNow();
			}
		}
	}
}
