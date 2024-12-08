package com.example.prepics.apis;

import com.example.prepics.annotations.Admin;
import com.example.prepics.annotations.User;
import com.example.prepics.entity.Tag;
import com.example.prepics.services.api.TagApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/api/tags")
public class TagApi {

    @Autowired
    private TagApiService tagApiService;

    /**
     * Tạo một thẻ (Tag) mới.
     *
     * API này cho phép người dùng tạo một thẻ mới.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền tạo thẻ hay không.
     * - Tạo thẻ mới trong hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param tagName: Tên của thẻ cần tạo.
     * @return ResponseEntity: Trả về phản hồi cho việc tạo thẻ.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
     */
    @User
    @PostMapping
    public ResponseEntity<?> createTag(Authentication authentication, @RequestBody String tagName) {
        return tagApiService.createTag(authentication, tagName);
    }

    /**
     * Cập nhật thông tin thẻ.
     *
     * API này cho phép người dùng cập nhật thông tin của thẻ.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền cập nhật thẻ đó hay không.
     * - Cập nhật các thông tin của thẻ trong hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param id: ID của thẻ cần cập nhật.
     * @param model: Dữ liệu thẻ mới (bao gồm tên thẻ).
     * @return ResponseEntity: Trả về phản hồi cho việc cập nhật thẻ.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy thẻ hoặc người dùng.
     */
    @User
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(Authentication authentication, @PathVariable Long id, @RequestBody Tag model) {
        model.setId(id);  // Đảm bảo ID đúng
        return tagApiService.updateTag(authentication, model);
    }

    /**
     * Xóa thẻ.
     *
     * API này cho phép người dùng xóa một thẻ.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền xóa thẻ đó hay không.
     * - Xóa thẻ khỏi hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param id: ID của thẻ cần xóa.
     * @return ResponseEntity: Trả về phản hồi cho việc xóa thẻ.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy thẻ hoặc người dùng.
     */
    @Admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(Authentication authentication, @PathVariable Long id) {
        return tagApiService.deleteTag(authentication, id);
    }

    /**
     * Lấy thông tin chi tiết của thẻ.
     *
     * API này cho phép người dùng lấy thông tin chi tiết của một thẻ.
     * Quy trình xử lý:
     * - Kiểm tra xem thẻ có tồn tại hay không.
     * - Trả về thông tin thẻ.
     *
     * @param id: ID của thẻ cần lấy thông tin.
     * @return ResponseEntity: Trả về thông tin chi tiết của thẻ.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy thẻ.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTag(@PathVariable Long id) {
        return tagApiService.getTag(id);
    }
}