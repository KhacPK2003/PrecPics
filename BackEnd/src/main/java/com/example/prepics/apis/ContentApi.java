package com.example.prepics.apis;

import com.example.prepics.annotations.Admin;
import com.example.prepics.dto.ContentDTO;
import com.example.prepics.services.api.ContentApiService;
import com.example.prepics.services.entity.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequestMapping("/public/api/contents")
public class ContentApi {

    @Autowired
    ContentService contentService;

    @Autowired
    ContentApiService contentApiService;

    /**
     * Upload content (image/video)
     * API này cho phép người dùng tải lên các nội dung (hình ảnh hoặc video) và lưu trữ vào hệ thống.
     * Các tham số trong model:
     * - "type": Loại nội dung (0 - ảnh, 1 - video).
     * - "tags": Các thẻ của nội dung, các thẻ cách nhau bằng dấu phẩy.
     * Quy trình xử lý:
     * - Tải lên file (ảnh hoặc video) và lưu thông tin vào cơ sở dữ liệu.
     * - Tạo mới đối tượng `Content` và lưu thông tin về file đã tải lên (ví dụ: ID, tên, chiều cao, chiều rộng, URL).
     * - Thêm các thẻ (tags) vào nội dung.
     * - Lưu thông tin vào Elasticsearch để hỗ trợ tìm kiếm.
     *

     */
    @PostMapping(value = "/upload", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadContent(
            Authentication authentication,@RequestPart ContentDTO request, @RequestPart MultipartFile file) throws IOException, ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.uploadContent(authentication, file, request));
    }

    /**
     * Delete content by ID
     * API này cho phép người dùng xóa nội dung dựa trên ID.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền xóa nội dung hay không.
     * - Xóa nội dung trong hệ thống cơ sở dữ liệu và Elasticsearch.
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param id: ID của nội dung cần xóa.
     * @return ResponseEntity: Trả về phản hồi cho việc xóa nội dung.
     * @throws IOException: Nếu có lỗi khi thao tác với file.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy nội dung.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContent(
            Authentication authentication,
            @PathVariable("id") String id) throws IOException, ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.deleteContent(authentication, id));
    }

    /**
     * Update tags of a content
     *
     * API này cho phép người dùng cập nhật thẻ (tags) của một nội dung.
     * Quy trình xử lý:
     * - Kiểm tra xem người dùng có quyền cập nhật thẻ của nội dung đó hay không.
     * - Cập nhật các thẻ của nội dung trong hệ thống.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param contentId: ID của nội dung cần cập nhật thẻ.
     * @param tags: Các thẻ mới cho nội dung, phân tách bằng dấu phẩy.
     * @return ResponseEntity: Trả về phản hồi cho việc cập nhật thẻ.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy nội dung hoặc người dùng.
     */
    @PutMapping("/update-tags/{contentId}")
    public ResponseEntity<?> updateTags(
            Authentication authentication,
            @PathVariable("contentId") String contentId,
            @RequestParam("tags") String tags) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.updateTags(authentication, contentId, tags));
    }

    /**
     * Get all content
     * API này trả về tất cả các nội dung có trong hệ thống.
     * @return ResponseEntity: Trả về danh sách tất cả nội dung.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy nội dung.
     */
    @GetMapping("/all")
    public ResponseEntity<?> findAllContent() throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findAllContent());
    }

    /**
     * Get content by type (image/video) (0/1)
     * API này trả về các nội dung có loại (hình ảnh hoặc video) dựa trên tham số truyền vào.
     * @param type: Loại nội dung (0 - hình ảnh, 1 - video).
     * @return ResponseEntity: Trả về các nội dung với loại đã chọn.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy nội dung với loại đã chỉ định.
     */
    @GetMapping("/by-type")
    public ResponseEntity<?> findAllByType(
            @RequestParam("type") int type,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size)
            throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findAllByType((type == 0), page, size));
    }

    /**
     * Get content by tags
     * API này trả về các nội dung có thẻ (tags) phù hợp với các thẻ được chỉ định.
     *
     * @param tags: Các thẻ cần tìm kiếm, phân tách bằng dấu phẩy.
     * @return ResponseEntity: Trả về các nội dung với thẻ phù hợp.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy nội dung với các thẻ đã chỉ định.
     */
    @GetMapping("/by-tags")
    public ResponseEntity<?> findAllByTags(
            @RequestParam("tags") String tags,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size)
            throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findAllByTags(tags,page, size));
    }

    /**
     * Get content by ID
     * API này cho phép người dùng lấy nội dung dựa trên ID của nó.
     *
     * @param id: ID của nội dung cần tìm kiếm.
     * @return ResponseEntity: Trả về nội dung với ID tương ứng.
     * @throws ChangeSetPersister.NotFoundException: Nếu không tìm thấy nội dung.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findContentById(
            @PathVariable("id") String id) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(contentApiService.findContentById(id));
    }

    /**
     * Get image with specified size
     * API này cho phép người dùng lấy ảnh đã thay đổi kích thước dựa trên các tham số chiều rộng và chiều cao.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param model: Map chứa thông tin cần thiết để thay đổi kích thước ảnh, bao gồm:
     *               - "content": Thông tin của nội dung ảnh (Content) cần thay đổi kích thước.
     *               - "width": Chiều rộng mới của ảnh.
     *               - "height": Chiều cao mới của ảnh.
     * @return ResponseEntity<byte[]>: Dữ liệu ảnh đã thay đổi kích thước dưới dạng mảng byte, trả về với kiểu `image/jpeg`.
     */
    @GetMapping(value = "/image/resize", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageWithSize(
            Authentication authentication,
            @RequestBody Map<String, Object> model) throws IOException, ChangeSetPersister.NotFoundException {

        // Gọi service để lấy ảnh đã thay đổi kích thước
        byte[] image = contentApiService.getImageWithSize(authentication, model);

        // Kiểm tra xem ảnh có tồn tại hay không
        if (image == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Trả về lỗi 404 nếu không tìm thấy ảnh
        }

        // Trả về dữ liệu ảnh với định dạng image/jpeg
        return ResponseEntity.ok(image);
    }

    /**
     * Get video with specified size
     * API này cho phép người dùng lấy video đã thay đổi kích thước dựa trên các tham số chiều rộng và chiều cao.
     *
     * @param authentication: Thông tin người dùng đã đăng nhập.
     * @param model: Map chứa thông tin cần thiết để thay đổi kích thước video, bao gồm:
     *               - "content": Thông tin của nội dung video (Content) cần thay đổi kích thước.
     *               - "width": Chiều rộng mới của video.
     *               - "height": Chiều cao mới của video.
     * @return ResponseEntity<byte[]>: Dữ liệu video đã thay đổi kích thước dưới dạng mảng byte, trả về với kiểu `video/mp4`.
     */
    @GetMapping(value = "/video/resize", produces = "video/mp4")
    public ResponseEntity<byte[]> getVideoWithSize(
            Authentication authentication,
            @RequestBody Map<String, Object> model) throws IOException, ChangeSetPersister.NotFoundException {

        // Gọi service để lấy video đã thay đổi kích thước
        byte[] video = contentApiService.getVideoWithSize(authentication, model);

        // Kiểm tra xem video có tồn tại hay không
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Trả về lỗi 404 nếu không tìm thấy video
        }

        // Trả về dữ liệu video với định dạng video/mp4
        return ResponseEntity.ok(video);
    }

    /**
     * API để thêm tags vào Elasticsearch.
     * Chỉ admin mới có thể truy cập API này.
     *
     * @param authentication: Thông tin người dùng hiện tại.
     * @return Map: Trả về kết quả thành công hoặc thất bại.
     */
    @Admin
    @PostMapping("elastic/insert")
    public ResponseEntity<?> doInsertTagsIntoElastic(Authentication authentication) {
        return ResponseEntity.ok(contentApiService.doInsertTagsIntoElastic(authentication));
    }

    /**
     * API để xóa tất cả tags trong Elasticsearch.
     * Chỉ admin mới có thể truy cập API này.
     *
     * @param authentication: Thông tin người dùng hiện tại.
     * @return Map: Trả về kết quả thành công hoặc thất bại.
     */
    @Admin
    @DeleteMapping("elastic/delete")
    public ResponseEntity<?> doDeleteTagsInElastic(Authentication authentication) {
        return ResponseEntity.ok(contentApiService.doDeleteTagsInElastic(authentication));
    }

    /**
     * API để thêm tags vào Elasticsearch.
     * Chỉ admin mới có thể truy cập API này.
     *
     * @param authentication: Thông tin người dùng hiện tại.
     * @return Map: Trả về kết quả thành công hoặc thất bại.
     */
    @Admin
    @GetMapping("/search/fuzzy")
    public ResponseEntity<?> doSearchWithFuzzy(@RequestParam String indexName, @RequestParam String fieldName,
                                               @RequestParam String approximates,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(contentApiService.doSearchWithFuzzy(indexName, fieldName, approximates, page, size));
    }
}

