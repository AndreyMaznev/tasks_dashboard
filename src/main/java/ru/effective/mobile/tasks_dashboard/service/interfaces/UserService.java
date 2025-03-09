package ru.effective.mobile.tasks_dashboard.service.interfaces;

import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import java.util.Set;

public interface UserService {
    //todo move to dto
    User getUserById(long id);
    User createUser(User user);
    User getUserByEmail(String email);
    void deleteUserById(long userId);
    void addRoleToUser(long userId, Role role);
    Set <String> getConvertedRoles(Set <Role> roles);
    Set<Role> getUserRolesByUserId(long userId);
    Set<Role> getUserRolesByEmail(String email);
    boolean hasRole(long userId, Role role);
    void removeRoleFromUser(long userId, Role role);
}
