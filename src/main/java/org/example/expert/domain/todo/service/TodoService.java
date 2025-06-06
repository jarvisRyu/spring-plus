package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDateTime startDate, LocalDateTime endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if (weather == null && startDate == null && endDate == null) {//전부 입력받지 않았을때
            Page<Todo> todos = todoRepository.findAllTodos(pageable);
            return todos.map(TodoResponse::from);

        } else if (weather != null && startDate == null && endDate == null) { //날씨만 입력되었을 때
            Page<Todo> todos = todoRepository.findAllByWeather(weather, pageable);
            return todos.map(TodoResponse::from);

        } else if (weather == null && startDate != null && endDate != null) { //수정일만 입력되었을 때
            Page<Todo> todos = todoRepository.findAllByDate(startDate, endDate, pageable);
            return todos.map(TodoResponse::from);

        } else { //전부 입력되었을 때
            Page<Todo> todos = todoRepository.findALLByWeatherAndDate(weather, startDate, endDate, pageable);
            return todos.map(TodoResponse::from);
        }
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));
//        User user = todo.getUser();

        return TodoResponse.from(todo);
//                todo.getId(),
//                todo.getTitle(),
//                todo.getContents(),
//                todo.getWeather(),
//                new UserResponse(user.getId(), user.getEmail(), user.getNickname()),
//                todo.getCreatedAt(),
//                todo.getModifiedAt()
//        );
    }
}
