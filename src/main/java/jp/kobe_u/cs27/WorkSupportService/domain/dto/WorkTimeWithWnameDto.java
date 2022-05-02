package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;

/**
 * 作業名付きの作業時間
 *
 * @author ing
 */
@Data
public class WorkTimeWithWnameDto {
    /** どの作業を */
    private String wid;

    /** 作業名 */
    private String wname;

    /** 何秒作業したか */
    private int second;

    /**
     * 作業と時間からDTOを作成
     *
     * @param Work
     * @param second
     * @return WorkTimeWithWnameDto
     */
    public static WorkTimeWithWnameDto build(Work work, int second) {
        WorkTimeWithWnameDto dto = new WorkTimeWithWnameDto();

        dto.wid = work.getWid();
        dto.wname = work.getWname();
        dto.second = second;

        return dto;
    }
}
