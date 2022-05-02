package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkTimeChallenge;

/**
 * 目標作業時間のリポジトリ
 *
 * @author ing
 */
@Repository
public interface WorkTimeChallengeRepositry extends CrudRepository<WorkTimeChallenge, Long> {

    /* -------------------- Read -------------------- */

    /**
     * ユーザの最新の目標作業時間を所得
     *
     * @param uid
     * @return WorkTimeChallenge
     */
    public WorkTimeChallenge findFirstByUidOrderByCreatedAtDesc(String uid);

    /* -------------------- Delete -------------------- */

    /**
     * ユーザID指定で目標作業時間をすべて消去する
     *
     * @param uid
     */
    public void deleteByUid(String uid);
}