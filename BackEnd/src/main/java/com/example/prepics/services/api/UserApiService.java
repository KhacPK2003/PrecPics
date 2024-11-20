package com.example.prepics.services.api;

import com.example.prepics.entity.Followees;
import com.example.prepics.entity.Followers;
import com.example.prepics.entity.User;
import com.example.prepics.models.ResponseProperties;
import com.example.prepics.services.entity.FolloweeService;
import com.example.prepics.services.entity.FollowerService;
import com.example.prepics.services.entity.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserApiService {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FollowerService followerService;

    @Autowired
    private FolloweeService followeeService;

    public ResponseEntity<?> loginUserWithGoogle(Authentication authentication)
            throws ChangeSetPersister.NotFoundException {
        // Lấy thông tin người dùng từ Authentication
        User userDecode = (User) authentication.getPrincipal();

        // Tìm kiếm hoặc tạo người dùng nếu chưa tồn tại
        User user = userService.findByEmail(User.class, userDecode.getEmail())
                .orElseGet(() -> {
                    try {
                        return userService.create(userDecode)
                                .orElseThrow(() -> new RuntimeException("Failed to create user"));
                    } catch (ChangeSetPersister.NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Trả phản hồi
        return ResponseProperties.createResponse(200, "Success", user);
    }

    public ResponseEntity<?> findAll(Authentication authentication) throws ChangeSetPersister.NotFoundException {
        // Lấy thông tin người dùng hiện tại
        User userDecode = (User) authentication.getPrincipal();
        User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Kiểm tra quyền admin
        if (!currentUser.getIsAdmin()) {
            return ResponseProperties.createResponse(403, "Forbidden", null);
        }

        // Tìm tất cả người dùng
        List<User> users = userService.findAll(User.class)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Trả phản hồi
        return ResponseProperties.createResponse(200, "Success", users);
    }

    public ResponseEntity<?> findById(String id) throws ChangeSetPersister.NotFoundException {
        // Tìm người dùng theo ID
        User user = userService.findById(User.class, id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Trả phản hồi
        return ResponseProperties.createResponse(200, "Success", user);
    }

    public ResponseEntity<?> update(Authentication authentication, User entity)
            throws ChangeSetPersister.NotFoundException {
        // Lấy thông tin người dùng hiện tại
        User userDecode = (User) authentication.getPrincipal();
        User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Kiểm tra quyền cập nhật
        if (!currentUser.getId().equals(entity.getId())) {
            return ResponseProperties.createResponse(403, "Forbidden", null);
        }

        // Cập nhật thông tin người dùng
        modelMapper.map(entity, currentUser);
        userService.update(currentUser);

        // Trả phản hồi
        return ResponseProperties.createResponse(200, "Success", currentUser);
    }


    public ResponseEntity<?> delete(String id) throws ChangeSetPersister.NotFoundException {
        User result = userService.findById(User.class, id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        userService.delete(id);
        return ResponseProperties.createResponse(200, "Success", result);
    }

    public ResponseEntity<?> doFollowUser(Authentication authentication, String userId)
            throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        User targetUser = userService.findById(User.class, userId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        //tao followee cho user
        Followees followees = new Followees();
        followees.setFolloweeId(userId);
        followees.setUserId(userDecode.getId());

        followeeService.create(followees);

        //tao follower cho user duoc follow
        Followers followers = new Followers();
        followers.setFollowerId(userDecode.getId());
        followers.setUserId(userId);

        followerService.create(followers);

        return ResponseProperties.createResponse(200, "Success", true);
    }

    public ResponseEntity<?> doUnfollowUser(Authentication authentication, String followeeId, String followerId)
            throws ChangeSetPersister.NotFoundException {
        User userDecode = (User) authentication.getPrincipal();
        User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Followees followee = followeeService.findByUserIdAndFolloweeId(Followees.class, followerId, followeeId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        followeeService.delete(followee.getId());

        Followers follower = followerService.findByUserIdAndFollowerId(Followers.class, followeeId, followerId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        followerService.delete(follower.getId());

        return ResponseProperties.createResponse(200, "Success", true);
    }
}
