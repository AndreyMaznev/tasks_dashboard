package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Comment;
import ru.effective.mobile.tasks_dashboard.model.Task;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.CommentRepository;
import ru.effective.mobile.tasks_dashboard.service.interfaces.TaskService;
import ru.effective.mobile.tasks_dashboard.util.CommentMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;


class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TaskServiceImpl taskServiceImpl;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCommentById() {
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText("Test comment");

        CommentOutputDto expectedDto = new CommentOutputDto();
        expectedDto.setId(commentId);
        expectedDto.setText("Test comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.commentToCommentOutputDto(comment)).thenReturn(expectedDto);

        CommentOutputDto result = commentService.getCommentById(commentId);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getText(), result.getText());

        verify(commentRepository).findById(commentId);
        verify(commentMapper).commentToCommentOutputDto(comment);
    }

    @Test
    void testCreateComment() {
        long taskId = 1L;
        CommentInputDto inputDto = new CommentInputDto();
        inputDto.setText("New comment");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("andrey@ya.ru");

        Task task = new Task();
        task.setId(taskId);
        task.setExecutor(currentUser);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("New comment");
        comment.setAuthor(currentUser);
        comment.setTask(task);
        comment.setCreatedAt(LocalDateTime.now());

        CommentOutputDto expectedDto = new CommentOutputDto();
        expectedDto.setId(1L);
        expectedDto.setText("New comment");

        when(taskServiceImpl.getTaskById(taskId)).thenReturn(task);
        when(commentMapper.commentInputDtoToComment(inputDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentOutputDto(comment)).thenReturn(expectedDto);
        when(taskServiceImpl.getTaskById(taskId)).thenReturn(task);

        mockSecurityContext(true);

        CommentOutputDto result = commentService.createComment(taskId, inputDto, currentUser);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getText(), result.getText());

        verify(taskServiceImpl).getTaskById(taskId);
        verify(commentMapper).commentInputDtoToComment(inputDto);
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToCommentOutputDto(comment);
    }

    @Test
    void testUpdateComment() {
        long taskId = 1L;
        long commentId = 1L;
        CommentInputDto inputDto = new CommentInputDto();
        inputDto.setText("Updated comment");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("admin@ya.ru");

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText("Old comment");
        Task tempTask = new Task();
        tempTask.setId(taskId);
        comment.setTask(tempTask);

        CommentOutputDto expectedDto = new CommentOutputDto();
        expectedDto.setId(commentId);
        expectedDto.setText("Updated comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentOutputDto(comment)).thenReturn(expectedDto);

        mockSecurityContext(true);

        CommentOutputDto result = commentService.updateComment(taskId, commentId, inputDto, currentUser);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getText(), result.getText());

        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToCommentOutputDto(comment);
    }

    @Test
    void testDeleteComment() {
        long taskId = 1L;
        long commentId = 1L;

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("admin@ya.ru");

        Comment comment = new Comment();
        comment.setId(commentId);
        Task tempTask = new Task();
        tempTask.setId(taskId);
        comment.setTask(tempTask);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        mockSecurityContext(true);

        commentService.deleteComment(taskId, commentId, currentUser);

        verify(commentRepository).findById(commentId);
        verify(commentRepository).delete(comment);
    }

    private void mockSecurityContext(boolean isAdmin) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        if (isAdmin) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
            Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
            Mockito.when(authentication.getAuthorities())
                    .thenReturn((Collection) authCollection);
        } else {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
            Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
            Mockito.when(authentication.getAuthorities())
                    .thenReturn((Collection) authCollection);
        }
    }
}