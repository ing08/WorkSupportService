package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jp.kobe_u.cs27.WorkSupportService.application.form.WnameForm;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkEvent;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkEventService;

/**
 * 作業イベントを操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkEventRestController {
    /** 作業イベントのサービス */
    private final WorkEventService wes;

    /* -------------------- Create -------------------- */

    /**
     * 作業をstart
     *
     * @param uid
     * @param WnameForm
     * @return WorkEvent
     */
    @PostMapping("/workevents/{uid}/start")
    public WorkEvent startWork(@PathVariable String uid, @RequestBody @Validated WnameForm form) {
        return wes.startWorkEventByWname(uid, form.getWname());
    }

    /**
     * 作業をend
     *
     * @param uid
     * @param WnameForm
     * @return WorkEvent
     */
    @PostMapping("/workevents/{uid}/end")
    public WorkEvent endWork(@PathVariable String uid, @RequestBody @Validated WnameForm form) {
        return wes.endWorkEventByWname(uid, form.getWname());
    }
}
