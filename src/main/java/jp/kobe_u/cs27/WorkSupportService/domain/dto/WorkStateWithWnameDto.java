package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;

/**
 * 作業名付きのユーザの作業状況
 *
 * @author ing
 */
@Data
public class WorkStateWithWnameDto {
    /** ユーザID */
    private String uid;

    /** 作業ID */
    private String wid;

    /** 作業名 */
    private String wname;

    /**
     * UserとWorkからDTOを生成
     *
     * @param uid
     * @param Work
     * @return WorkStateWithNameDto
     */
    public static WorkStateWithWnameDto build(String uid, Work work) {
        WorkStateWithWnameDto dto = new WorkStateWithWnameDto();

        dto.uid = uid;
        dto.wid = work.getWid();
        dto.wname = work.getWname();

        return dto;
    }
}
