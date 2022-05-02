package jp.kobe_u.cs27.WorkSupportService.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.*;

/**
 * 作業のエンティティ
 *
 * @author ing
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Work {
    /** 作業のID */
    @Id
    private String wid;

    /** 作業名 */
    private String wname;
}
