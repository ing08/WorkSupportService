package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Comment;

/**
 * コメントのリポジトリ
 *
 * @author ing
 */
@Repository
public interface CommentRepositry extends CrudRepository<Comment, Long> {

        /* -------------------- Read -------------------- */

        /**
         * uidに紐づくすべてのCommentを所得
         *
         * @param uid
         * @return List<Comment>
         */
        public List<Comment> findAllByUidOrderByCreatedAtDesc(String uid);

        /**
         * ユーザ指定で現在の最新のCommentを所得
         *
         * @param uid
         * @return Comment
         */
        public Comment findFirstByUidAndDiscloseflagOrderByCreatedAtDesc(String uid, Boolean discloseflag);

        // /**
        // * uidとwidに紐づくすべてのCommentを所得
        // *
        // * @param uid
        // * @param wid
        // * @return List<Comment>
        // */
        // public List<Comment> findAllByUidAndByWidOrderByCreatedAtDesc(String uid,
        // String wid);

        /**
         * ユーザのコメントを期間を指定して所得
         *
         * @param uid
         * @param since
         * @param until
         * @return List<Comment>
         */
        public List<Comment> findByUidAndCreatedAtBetweenOrderByCreatedAtDesc(String uid, Date since, Date until);

        /**
         * uidとwidに紐づくすべてのCommentを所得
         *
         * @param uid
         * @param wid
         * @return List<Comment>
         */
        public List<Comment> findAllByUidAndWidOrderByCreatedAtDesc(String uid, String wid);

        /**
         * ユーザのコメントを作業と期間を指定して所得
         *
         * @param uid
         * @param wid
         * @return List<Comment>
         */
        public List<Comment> findByUidAndWidAndCreatedAtBetweenOrderByCreatedAtDesc(String uid, String wid, Date since,
                        Date until);

        /**
         * ユーザの成果を所得
         *
         * @param uid
         * @param achivementflag
         * @return List<Comment>
         */
        public List<Comment> findByUidAndAchievementflagAndCreatedAtBetweenOrderByCreatedAtAsc(String uid,
                        Boolean achivementflag, Date since, Date until);

        /**
         * ユーザの成果以外の公開しているコメントを所得
         *
         * @param uid
         * @param discloseflag
         * @param achivementflag
         * @param since
         * @param until
         * @return List<Comment>
         */
        public List<Comment> findByUidAndDiscloseflagAndAchievementflagAndCreatedAtBetweenOrderByCreatedAtAsc(
                        String uid, Boolean discloseflag, Boolean achivementflag, Date since, Date until);

        /* -------------------- Delete -------------------- */

        /**
         * ユーザID指定でCommentをすべて消去する
         *
         * @param uid
         */
        public void deleteByUid(String uid);
}
