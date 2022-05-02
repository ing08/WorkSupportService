package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkEvent;

/**
 * 作業イベントログのリポジトリ
 *
 * @author ing
 */
@Repository
public interface WorkEventRepositry extends CrudRepository<WorkEvent, Long> {

    /* -------------------- Read -------------------- */

    /**
     * ユーザ指定で現在の最新のWorkEventを所得
     *
     * @param uid
     * @return WorkEvent
     */
    public WorkEvent findFirstByUidOrderByCreatedAtDesc(String uid);

    /**
     * ユーザの作業イベントを日時検索
     *
     * @param uid
     * @param since
     * @param until
     * @return List<WorkEvent>
     */
    public List<WorkEvent> findByUidAndCreatedAtBetweenOrderByCreatedAtDesc(String uid, Date since, Date until);

    /**
     * ユーザの作業イベントで指定日時以前最初にあるものを所得
     *
     * @param uid
     * @param date
     * @return WorkEvent
     */
    public WorkEvent findFirstByUidAndCreatedAtLessThanEqualOrderByCreatedAtDesc(String uid, Date date);

    /**
     * ユーザの作業イベントで指定日時以降最初にあるものを所得
     *
     * @param uid
     * @param date
     * @return WorkEvent
     */
    public WorkEvent findFirstByUidAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(String uid, Date date);

    /* -------------------- Delete -------------------- */

    /**
     * ユーザID指定でWorkEventをすべて消去する
     *
     * @param uid
     */
    public void deleteByUid(String uid);
}
