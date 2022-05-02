package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkStateWithNameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkStateWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkStateService;

/**
 * 作業状況を操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkStateRestController {
    /** 作業状況のサービス */
    private final WorkStateService wss;

    /* -------------------- Read -------------------- */

    /**
     * あるユーザの勉強状況を所得
     *
     * @param uid
     * @return WorkStateWithWnameDto
     */
    @GetMapping("/workstates/{uid}")
    public WorkStateWithWnameDto getAllWorkState(@PathVariable String uid) {
        return wss.getWorkStateWithWname(uid);
    }

    /**
     * 全ての勉強状況を所得
     *
     * @return List<WorkStateWithNameDto>
     */
    @GetMapping("/workstates")
    public List<WorkStateWithNameDto> getAllWorkState() {
        return wss.getAllWorkStateWithName();
    }

    /**
     * 現在自分以外の作業中のユーザの人数を所得
     *
     * @return 現在作業中のユーザの人数
     */
    @GetMapping("/workstates/{uid}/number")
    public int getNumberOfWorking(@PathVariable String uid) {
        return wss.getNumberOfWorking(uid);
    }
}
