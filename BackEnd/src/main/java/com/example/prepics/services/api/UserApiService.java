package com.example.prepics.services.api;

import com.example.prepics.dto.UserStatisticsDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public ResponseEntity<?> loginUserWithGoogle(Authentication authentication) {
        try {
            User userDecode = (User) authentication.getPrincipal();
            User user = userService.findByEmail(User.class, userDecode.getEmail())
                    .orElseGet(() -> {
                        try {
                            return userService.create(userDecode)
                                    .orElseThrow(() -> new RuntimeException("Failed to create user"));
                        } catch (ChangeSetPersister.NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return ResponseProperties.createResponse(200, "Success", user);
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    public ResponseEntity<?> findAll(Authentication authentication) {
        try {
            User userDecode = (User) authentication.getPrincipal();
            User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            if (!currentUser.getIsAdmin()) {
                return ResponseProperties.createResponse(403, "Forbidden", null);
            }

            List<User> users = userService.findAll(User.class)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            users.forEach(user -> {
                try {
                    UserStatisticsDTO dto = userService.getUserStatistics(user.getId())
                            .orElseThrow(ChangeSetPersister.NotFoundException::new);
                    user.setTotalContents(dto.getTotalContents());
                    user.setTotalFollowers(dto.getTotalFollowers());
                    user.setTotalLikes(dto.getTotalLikes());
                    user.setTotalFollowing(dto.getTotalFollowing());
                } catch (ChangeSetPersister.NotFoundException ignored) {
                    // Bỏ qua nếu không tìm thấy thông tin thống kê
                }
            });

            return ResponseProperties.createResponse(200, "Success", users);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(403, "Forbidden", e.getMessage());
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    public ResponseEntity<?> findById(String id) {
        try {
            User user = userService.findById(User.class, id)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            UserStatisticsDTO dto = userService.getUserStatistics(user.getId())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            user.setTotalContents(dto.getTotalContents());
            user.setTotalFollowers(dto.getTotalFollowers());
            user.setTotalLikes(dto.getTotalLikes());
            user.setTotalFollowing(dto.getTotalFollowing());

            return ResponseProperties.createResponse(200, "Success", user);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, "Not Found", e.getMessage());
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    public ResponseEntity<?> update(Authentication authentication, User entity) {
        try {
            User userDecode = (User) authentication.getPrincipal();
            User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            if (!currentUser.getId().equals(entity.getId())) {
                return ResponseProperties.createResponse(403, "Forbidden", null);
            }

            modelMapper.map(entity, currentUser);
            userService.update(currentUser);

            return ResponseProperties.createResponse(200, "Success", currentUser);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, "Not Found", e.getMessage());
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    @Transactional("masterTransactionManager")
    public ResponseEntity<?> delete(String id) {
        try {
            User result = userService.findById(User.class, id)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            userService.delete(id);
            return ResponseProperties.createResponse(200, "Success", result);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, "Not Found", e.getMessage());
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    public ResponseEntity<?> doFollowUser(Authentication authentication, String userId) {
        try {
            User userDecode = (User) authentication.getPrincipal();
            User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            User targetUser = userService.findById(User.class, userId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            Followees followee = followeeService.findByUserIdAndFolloweeId(Followees.class, userId, userDecode.getId())
                    .orElse(null);

            Followers follower = followerService.findByUserIdAndFollowerId(Followers.class, userDecode.getId(), userId)
                    .orElse(null);

            if (followee == null && follower == null) {
                Followees followees = new Followees();
                followees.setFolloweeId(userId);
                followees.setUserId(userDecode.getId());
                followeeService.create(followees);

                Followers followers = new Followers();
                followers.setFollowerId(userDecode.getId());
                followers.setUserId(userId);
                followerService.create(followers);

                return ResponseProperties.createResponse(200, "Success", true);
            }

            return ResponseProperties.createResponse(403, "Forbidden", null);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, "Not Found", e.getMessage());
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    @Transactional("masterTransactionManager")
    public ResponseEntity<?> doUnfollowUser(Authentication authentication, String followeeId, String followerId) {
        try {
            User userDecode = (User) authentication.getPrincipal();
            User currentUser = userService.findByEmail(User.class, userDecode.getEmail())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);

            Followees followee = followeeService.findByUserIdAndFolloweeId(Followees.class, followerId, followeeId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            followeeService.delete(followee.getId()).orElseThrow(ChangeSetPersister.NotFoundException::new);

            Followers follower = followerService.findByUserIdAndFollowerId(Followers.class, followeeId, followerId)
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            followerService.delete(follower.getId()).orElseThrow(ChangeSetPersister.NotFoundException::new);

            return ResponseProperties.createResponse(200, "Success", true);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseProperties.createResponse(404, "Not Found", e.getMessage());
        } catch (Exception e) {
            return ResponseProperties.createResponse(500, "Internal Server Error", e.getMessage());
        }
    }
}
