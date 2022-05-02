package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkLog;

/**
 * 作業イベントログのリポジトリ
 *
 * @author ing
 */
@Repository
public interface WorkLogRepositry extends CrudRepository<WorkLog, Long> {

        /* -------------------- Read -------------------- */

        /**
         * uidに紐づくすべてのWorkLogを所得
         *
         * @param uid
         * @return List<WorkLog>
         */
        public List<WorkLog> findByUidOrderBySinceDesc(String uid);

        /**
         * ユーザ指定で現在の最新のWorkLogを所得
         *
         * @param uid
         * @return WorkLog
         */
        public WorkLog findFirstByUidOrderBySinceDesc(String uid);

        /**
         * ユーザの作業ログを期間を指定して所得
         *
         * @param uid
         * @param since
         * @param until
         * @return List<WorkLog>
         */
        public List<WorkLog> findByUidAndUntilBetweenOrderBySinceDesc(String uid, Date since, Date until);

        /**
         * uidとwidに紐づくすべてのWorkLogを所得
         *
         * @param uid
         * @param wid
         * @return List<WorkLog>
         */
        public List<WorkLog> findByUidAndWidOrderBySinceDesc(String uid, String wid);

        /**
         * ユーザの作業ログを作業と期間を指定して所得
         *
         * @param uid
         * @param wid
         * @param since
         * @param until
         * @return List<WorkLog>
         */
        public List<WorkLog> findByUidAndWidAndUntilBetweenOrderBySinceDesc(String uid, String wid, Date since,
                        Date until);

        /* -------------------- Delete -------------------- */

        /**
         * ユーザID指定でWorkLogをすべて消去する
         *
         * @param uid
         */
        public void deleteByUid(String uid);
}
