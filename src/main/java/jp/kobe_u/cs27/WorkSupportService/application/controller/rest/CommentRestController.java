package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.WorkSupportService.application.form.AchivementForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.CidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.CommentForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.QueryForm;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.CommentWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;
import jp.kobe_u.cs27.WorkSupportService.domain.service.CommentService;

/**
 * コメントを操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentRestController {
    /** コメントのサービス */
    private final CommentService cs;

    /* -------------------- Create -------------------- */

    /**
     * コメントの追加
     *
     * @param uid
     * @param CommentForm
     * @return Comment
     */
    @PostMapping("/comments/{uid}")
    public Comment addComment(@PathVariable String uid, @RequestBody @Validated CommentForm form) {
        Comment comment = form.toEntity(); // フォームをエンティティに変換

        comment.setUid(uid);

        return cs.createComment(comment);
    }

    /**
     * 成果の追加
     *
     * @param uid
     * @param AchivementForm
     * @return Comment
     */
    @PostMapping("/comments/achivements/{uid}")
    public Comment addAchivement(@PathVariable String uid, @RequestBody @Validated AchivementForm form) {
        Comment comment = form.toEntity(); // フォームをエンティティに変換

        comment.setUid(uid);

        return cs.createAchivement(comment);
    }

    /* -------------------- Read -------------------- */

    /**
     * ユーザに紐づくすべてのコメントを作業名付きで所得
     *
     * @param uid
     * @return List<CommentWithWnameDto>
     */
    @GetMapping("/comments/{uid}")
    public List<CommentWithWnameDto> getAllCommentByUid(@PathVariable String uid) {
        return cs.getAllCommentByUid(uid);
    }

    /**
     * ユーザのコメントを期間を指定して所得
     *
     * @param uid
     * @param since
     * @param until
     * @return List<CommentWithWnameDto>
     */
    @GetMapping("/comments/{uid}/query")
    public List<CommentWithWnameDto> getWorkLogSettingPeriod(@PathVariable String uid,
            @RequestBody @Validated QueryForm form) {
        return cs.getCommentSettingPeriod(uid, form.getSince(), form.getUntil());
    }

    /**
     * ユーザのコメントを作業と期間を指定して所得
     *
     * @param uid
     * @param wid
     * @param QueryForm
     * @return List<CommentWithWnameDto>
     */
    @GetMapping("/comments/{uid}/query/{wid}")
    public List<CommentWithWnameDto> getCommentSettingPeriodAndWid(@PathVariable String uid, @PathVariable String wid,
            @RequestBody @Validated QueryForm form) {
        String since = form.getSince();

        if (since.equals("all")) { // 全期間対象なら
            if (wid.equals("all")) { // 全作業対象なら
                return cs.getAllCommentByUid(uid);
            } else if (wid.equals("unspecified")) { // 未指定対象なら
                return cs.getCommentByWid(uid, "0");
            }

            return cs.getCommentByWid(uid, wid);
        } else {
            if (wid.equals("all")) { // 全作業対象なら
                return cs.getCommentSettingPeriod(uid, form.getSince(), form.getUntil());
            } else if (wid.equals("unspecified")) { // 未指定対象なら
                return cs.getCommentByWidSettingPeriod(uid, "0", form.getSince(), form.getUntil());
            }

            return cs.getCommentByWidSettingPeriod(uid, wid, form.getSince(), form.getUntil());
        }
    }

    /* -------------------- Delete -------------------- */

    /**
     * コメントを削除
     *
     * @param uid
     * @param @param true
     */
    @DeleteMapping("/comments/{uid}")
    public Boolean deleteComment(@PathVariable String uid, @RequestBody @Validated CidForm form) {
        cs.deleteComment(uid, form.getCid()); // コメントを削除

        return true;
    }
}
