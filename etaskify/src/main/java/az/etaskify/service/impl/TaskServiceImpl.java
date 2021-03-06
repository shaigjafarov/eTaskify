package az.etaskify.service.impl;

import az.etaskify.dto.TaskDto;
import az.etaskify.exception.UserNotExistException;
import az.etaskify.mapper.TaskMapper;
import az.etaskify.mapper.UserMapper;
import az.etaskify.model.Organization;
import az.etaskify.model.Task;
import az.etaskify.model.User;
import az.etaskify.repository.TaskRepository;
import az.etaskify.repository.UserRepository;
import az.etaskify.service.EmailService;
import az.etaskify.service.OrganizationService;
import az.etaskify.service.TaskService;
import az.etaskify.util.SecurityContextUtility;
import az.etaskify.util.ValidationObjects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final OrganizationService organizationService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    @Override
    public ResponseEntity<String> saveOrUpdateTask(TaskDto taskDto) {
        Organization organization =
                organizationService.findOrganizationByEmail(SecurityContextUtility.getLoggedUsername());
        ValidationObjects.controlObjectNotNull(organization, "organization not exist");
        List<User> organizationUsers = organization.getUsers();
        List<User> assignees = UserMapper.INSTANCE.toUserList(taskDto.getUserDtoList());
        checkUsersExist(organizationUsers, assignees);
        Task task = TaskMapper.INSTANCE.toEntity(taskDto);
        Optional<User> optionalUser = userRepository.findUserEntityByEmail(SecurityContextUtility.getLoggedUsername());
        task.setCreatedBy(optionalUser.orElseThrow());
        task.setAssignees(assignees);
        task.setOrganization(organization);
        taskRepository.save(task);
        if (!assignees.isEmpty()) {
            sendMailTaskAssigned(task);
        }
        return new ResponseEntity<>("Task is created successful", HttpStatus.CREATED);
    }

    private void sendMailTaskAssigned(Task task) {
        emailService.sendMail(
                task.getTitle(),
                task.getDescription(),
                task.getAssignees().stream().map(User::getEmail).collect(Collectors.toList())
        );
    }

    @Override
    public ResponseEntity<List<TaskDto>> getTasksOrganization() {
        Organization organization =
                organizationService.findOrganizationByEmail(SecurityContextUtility.getLoggedUsername());
        List<Task> taskList = taskRepository.findAllByOrganization(organization);
        return new ResponseEntity<>(TaskMapper.INSTANCE.toTaskDtoList(taskList), HttpStatus.OK);
    }

    private void checkUsersExist(List<User> existUsers, List<User> receivedUsers) {
        List<User> userList = receivedUsers.stream().filter(existUsers::contains).collect(Collectors.toList());
        if (userList.isEmpty()) {
            throw new UserNotExistException(userList.toString());
        }

    }
}
