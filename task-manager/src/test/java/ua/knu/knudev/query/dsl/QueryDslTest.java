//package ua.knu.knudev.query.dsl;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ua.knu.knudev.taskmanager.domain.QTask;
//import ua.knu.knudev.taskmanager.domain.Task;
//import ua.knu.knudev.taskmanager.repository.TaskRepository;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//public class QueryDslTest {
//
//    UUID task1Id = UUID.randomUUID();
//    UUID task2Id = UUID.randomUUID();
//    UUID task3Id = UUID.randomUUID();
//    QTask qTask = QTask.task;
//
//    @Mock
//    JPAQueryFactory jpaQueryFactory;
//
//    @Mock
//    TaskRepository taskRepository;
//
//    @BeforeEach
//    void enterTestDataInDB() {
//        Task task1 = new Task();
//        Task task2 = new Task();
//        Task task3 = new Task();
//
//        task1.setId(task1Id);
//        task1.setName("task1 Name");
//        task1.setBody("task1 Body");
//        task2.setId(task2Id);
//        task2.setName("task2 Name");
//        task2.setBody("task2 Body");
//        task3.setId(task3Id);
//        task3.setName("task3 Name");
//        task3.setBody("task3 Body");
//
//        Mockito.when(taskRepository.save(task1)).thenReturn(task1);
//        Mockito.when(taskRepository.save(task2)).thenReturn(task2);
//        Mockito.when(taskRepository.save(task3)).thenReturn(task3);
//    }
//
//    @Test
//    void should_return_all_tasks_in_db() {
//        List<Task> expectedTasks = List.of(
//                new Task(task1Id, "task1 Name", "task1 Body"),
//                new Task(task2Id, "task2 Name", "task2 Body"),
//                new Task(task3Id, "task3 Name", "task3 Body")
//        );
//
//        Mockito.when(jpaQueryFactory.selectFrom(qTask).fetch()).thenReturn(expectedTasks);
//
//        List<Task> allTasksFromDb = jpaQueryFactory.selectFrom(qTask).fetch();
//
//        assertThat(allTasksFromDb).hasSize(3);
//        assertThat(allTasksFromDb).containsExactlyElementsOf(expectedTasks);
//    }
//}
