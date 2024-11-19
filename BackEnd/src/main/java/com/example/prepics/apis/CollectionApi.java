package com.example.prepics.apis;

import com.example.prepics.annotations.User;
import com.example.prepics.entity.Collection;
import com.example.prepics.services.api.CollectionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/api/collections")
public class CollectionApi {

    @Autowired
    private CollectionApiService collectionApiService;

    /**
     * Tạo bộ sưu tập mới.
     *
     * API này cho phép người dùng tạo một bộ sưu tập mới.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền tạo bộ sưu tập hay không.
     * - Tạo bộ sưu tập mới trong hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param collectionName: Tên của bộ sưu tập cần tạo.
     * @return ResponseEntity: Trả về phản hồi cho việc tạo bộ sưu tập.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy người dùng.
     **/
    @User
    @PostMapping
    public ResponseEntity<?> createCollection(Authentication authentication, @RequestBody String collectionName) {
        return ResponseEntity.ok(collectionApiService.createCollection(authentication, collectionName));
    }

    /**
     * Cập nhật thông tin bộ sưu tập.
     * API này cho phép người dùng cập nhật thông tin bộ sưu tập.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền cập nhật bộ sưu tập đó hay không.
     * - Cập nhật các thông tin của bộ sưu tập trong hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param id: ID của bộ sưu tập cần cập nhật.
     * @param model: Dữ liệu bộ sưu tập mới (bao gồm tên bộ sưu tập).
     * @return ResponseEntity: Trả về phản hồi cho việc cập nhật bộ sưu tập.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy bộ sưu tập hoặc người dùng.
     */
    @User
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollection(Authentication authentication, @PathVariable Long id,
                                                @RequestBody Collection model) {
        model.setId(id);  // Đảm bảo ID đúng
        return ResponseEntity.ok(collectionApiService.updateCollection(authentication, model));
    }

    /**
     * Xóa bộ sưu tập.
     *
     * API này cho phép người dùng xóa bộ sưu tập.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền xóa bộ sưu tập đó hay không.
     * - Xóa bộ sưu tập khỏi hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param id: ID của bộ sưu tập cần xóa.
     * @return ResponseEntity: Trả về phản hồi cho việc xóa bộ sưu tập.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy bộ sưu tập hoặc người dùng.
     */
    @User
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(collectionApiService.deleteCollection(authentication, id));
    }

    /**
     * Lấy thông tin chi tiết của bộ sưu tập.
     *
     * API này cho phép người dùng lấy thông tin chi tiết của bộ sưu tập.
     * Quy trình xử lý:
     * - Kiểm tra xem bộ sưu tập có tồn tại hay không.
     * - Trả về thông tin bộ sưu tập.
     *
     * @param id: ID của bộ sưu tập cần lấy thông tin.
     * @return ResponseEntity: Trả về thông tin chi tiết bộ sưu tập.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy bộ sưu tập.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionApiService.getCollection(id));
    }

    /**
     * Thêm nội dung vào bộ sưu tập.
     * API này cho phép người dùng thêm nội dung vào bộ sưu tập của mình.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền thêm nội dung vào bộ sưu tập không.
     * - Thêm nội dung vào bộ sưu tập.
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param collectionId: ID của bộ sưu tập.
     * @param contentId: ID của nội dung cần thêm vào bộ sưu tập.
     * @return ResponseEntity: Trả về phản hồi cho việc thêm nội dung vào bộ sưu tập.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy bộ sưu tập hoặc nội dung.
     */
    @User
    @PostMapping("/{collectionId}/contents/{contentId}")
    public ResponseEntity<?> addContentToCollection(Authentication authentication, @PathVariable Long collectionId,
                                                      @PathVariable String contentId) {
        return ResponseEntity.ok(collectionApiService.addContentToCollection(authentication, collectionId, contentId));
    }

    /**
     * Xóa nội dung khỏi bộ sưu tập.
     * API này cho phép người dùng xóa nội dung khỏi bộ sưu tập của mình.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền xóa nội dung khỏi bộ sưu tập không.
     * - Xóa nội dung khỏi bộ sưu tập.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param collectionId: ID của bộ sưu tập.
     * @param contentId: ID của nội dung cần xóa khỏi bộ sưu tập.
     * @return ResponseEntity: Trả về phản hồi cho việc xóa nội dung khỏi bộ sưu tập.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy bộ sưu tập hoặc nội dung.
     */
    @User
    @DeleteMapping("/{collectionId}/contents/{contentId}")
    public ResponseEntity<?> removeContentFromCollection(Authentication authentication, @PathVariable Long collectionId,
                                                           @PathVariable String contentId) {
        return ResponseEntity.ok(collectionApiService.removeContentToCollection(authentication, collectionId, contentId));
    }
}

