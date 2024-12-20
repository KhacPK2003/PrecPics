package com.example.prepics.apis;

import com.example.prepics.annotations.Admin;
import com.example.prepics.annotations.Guest;
import com.example.prepics.entity.User;
import com.example.prepics.services.api.UserApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public/api/users")
public class UserApi {

  @Autowired
  private UserApiService userApiService;

  /**
   * Đăng nhập người dùng qua Google. API này cho phép người dùng đăng nhập vào hệ thống thông qua
   * tài khoản Google. Quy trình xử lý: - Lấy thông tin người dùng từ Authentication (dữ liệu người
   * dùng đã đăng nhập). - Tìm kiếm người dùng trong hệ thống qua email. - Nếu người dùng chưa tồn
   * tại, sẽ được tạo mới.
   *
   * @param authentication: Thông tin người dùng đã đăng nhập thông qua Google.
   * @return Map: Trả về kết quả đăng nhập, bao gồm thông tin người dùng.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
   */
  @PostMapping("/login")
  public ResponseEntity<?> loginUserWithGoogle(Authentication authentication)
      throws ChangeSetPersister.NotFoundException {
    return userApiService.loginUserWithGoogle(authentication);
  }

  /**
   * Lấy danh sách tất cả người dùng (dành cho admin). API này cho phép admin lấy tất cả thông tin
   * người dùng trong hệ thống. Quy trình xử lý: - Kiểm tra quyền admin của người dùng hiện tại. -
   * Nếu là admin, trả về danh sách tất cả người dùng. - Nếu không phải admin, trả về lỗi 403
   * (Forbidden).
   *
   * @param authentication: Thông tin người dùng hiện tại.
   * @return Map: Trả về danh sách tất cả người dùng nếu là admin.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
   */
  @Admin
  @GetMapping
  public ResponseEntity<?> findAll(Authentication authentication)
      throws ChangeSetPersister.NotFoundException {
    return userApiService.findAll(authentication);
  }

  /**
   * Lấy thông tin người dùng theo ID. API này cho phép người dùng hoặc admin lấy thông tin của một
   * người dùng dựa trên ID. Quy trình xử lý: - Kiểm tra người dùng có tồn tại không. - Nếu tồn tại,
   * trả về thông tin người dùng. - Nếu không tìm thấy, trả về lỗi 404 (Not Found).
   *
   * @param id: ID của người dùng cần lấy thông tin.
   * @return Map: Trả về thông tin của người dùng nếu tìm thấy.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
   */
//  @Guest
  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable String id)
      throws ChangeSetPersister.NotFoundException {
    return userApiService.findById(id);
  }

  /**
   * Cập nhật thông tin người dùng. API này cho phép người dùng cập nhật thông tin cá nhân của mình.
   * Quy trình xử lý: - Kiểm tra quyền của người dùng hiện tại có thể cập nhật thông tin hay không.
   * - Nếu người dùng không phải chủ sở hữu của tài khoản hoặc không phải admin, trả về lỗi 403. -
   * Nếu hợp lệ, cập nhật thông tin và trả về kết quả thành công.
   *
   * @param authentication: Thông tin người dùng hiện tại đã đăng nhập.
   * @param id:             ID của người dùng cần cập nhật.
   * @param entity:         Các thông tin mới cần cập nhật.
   * @return Map: Trả về kết quả cập nhật thành công.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
   */
  @com.example.prepics.annotations.User
  @PutMapping("/{id}")
  public ResponseEntity<?> update(Authentication authentication, @PathVariable String id,
      @RequestBody User entity)
      throws ChangeSetPersister.NotFoundException {
    entity.setId(id);  // Đảm bảo ID đúng
    return userApiService.update(authentication, entity);
  }

  /**
   * Xóa người dùng theo ID. API này cho phép xóa người dùng khỏi hệ thống. Quy trình xử lý: - Tìm
   * kiếm người dùng theo ID. - Nếu tìm thấy, xóa người dùng và trả về kết quả thành công. - Nếu
   * không tìm thấy người dùng, trả về lỗi 404.
   *
   * @param id: ID của người dùng cần xóa.
   * @return Map: Trả về kết quả xóa người dùng thành công.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
   */
  @Admin
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable String id)
      throws ChangeSetPersister.NotFoundException {
    return userApiService.delete(id);
  }

  /**
   * Theo dõi một người dùng khác. API này cho phép người dùng theo dõi một người dùng khác trong hệ
   * thống. Quy trình xử lý: - Kiểm tra xem người dùng hiện tại có tồn tại trong hệ thống không. -
   * Nếu người dùng và người được theo dõi tồn tại, tạo liên kết "theo dõi" (Followees) và "bị theo
   * dõi" (Followers). - Trả về kết quả thành công nếu theo dõi thành công.
   *
   * @param authentication: Thông tin người dùng hiện tại đã đăng nhập.
   * @param userId:         ID của người dùng mà người đăng nhập muốn theo dõi.
   * @return Map: Trả về kết quả thành công khi theo dõi người dùng.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
   */
  @com.example.prepics.annotations.User
  @PostMapping("/{userId}/follow")
  public ResponseEntity<?> doFollowUser(Authentication authentication, @PathVariable String userId)
      throws ChangeSetPersister.NotFoundException {
    return userApiService.doFollowUser(authentication, userId);
  }

  /**
   * Bỏ theo dõi một người dùng khác. API này cho phép người dùng bỏ theo dõi một người dùng khác
   * trong hệ thống. Quy trình xử lý: - Kiểm tra xem người dùng hiện tại có tồn tại trong hệ thống
   * không. - Xóa liên kết theo dõi và bị theo dõi giữa hai người dùng. - Trả về kết quả thành công
   * nếu bỏ theo dõi thành công.
   *
   * @param authentication: Thông tin người dùng hiện tại đã đăng nhập.
   * @param followeeId:     ID của người dùng bị bỏ theo dõi.
   * @param followerId:     ID của người dùng đang bỏ theo dõi.
   * @return Map: Trả về kết quả thành công khi bỏ theo dõi.
   * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng hoặc liên kết.
   */
  @com.example.prepics.annotations.User
  @DeleteMapping("/{followeeId}/unfollow/{followerId}")
  public ResponseEntity<?> doUnfollowUser(Authentication authentication,
      @PathVariable String followeeId
      , @PathVariable String followerId) throws ChangeSetPersister.NotFoundException {
    return userApiService.doUnfollowUser(authentication, followeeId, followerId);
  }
}