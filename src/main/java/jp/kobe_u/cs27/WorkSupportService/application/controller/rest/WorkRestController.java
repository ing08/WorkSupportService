package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jp.kobe_u.cs27.WorkSupportService.application.form.WnameForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkForm;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkService;

/**
 * 作業を操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkRestController {
    /** 作業のサービス */
    private final WorkService ws;

    /* -------------------- Create -------------------- */

    /**
     * 作業の追加
     *
     * @param WorkForm
     * @return Work
     */
    @PostMapping("/works")
    public Work addWork(@RequestBody @Validated WorkForm form) {
        return ws.createWork(form.toEntity());
    }

    /* -------------------- Read -------------------- */

    /**
     * 作業の所得
     *
     * @param wid
     * @return Work
     */
    @GetMapping("/works/{wid}")
    public Work getWork(@PathVariable String wid) {
        return ws.getWork(wid);
    }

    /**
     * 作業名で作業を所得
     *
     * @param WnameForm
     * @return Work
     */
    @GetMapping("/works/name")
    public Work getWorkByWname(@RequestBody @Validated WnameForm form) {
        return ws.getWorkByWname(form.getWname());
    }

    /**
     * すべての作業を所得
     *
     * @return List<Work>
     */
    @GetMapping("/works")
    public List<Work> getAllWork() {
        return ws.getAllWork();
    }

    /* -------------------- Update -------------------- */

    /**
     * 作業の情報更新
     *
     * @param wid, WorkForm
     * @return Work
     */
    @PutMapping("/works/{wid}")
    public Work updateWork(@PathVariable String wid, @RequestBody @Validated WorkForm form) {
        Work work = ws.updateWork(wid, form.toEntity());
        return work;
    }

    /* -------------------- Delete -------------------- */

    /**
     * 作業を削除する
     *
     * @param wid
     * @return boolean
     */
    @DeleteMapping("/works/{wid}")
    public Boolean deleteUser(@PathVariable String wid) {
        ws.deleteWork(wid);

        return true;
    }
}
