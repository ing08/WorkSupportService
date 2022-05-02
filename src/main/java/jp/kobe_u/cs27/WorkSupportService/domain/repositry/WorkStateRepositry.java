package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkState;

/**
 * 作業状況のリポジトリ
 *
 * @author ing
 */
@Repository
public interface WorkStateRepositry extends CrudRepository<WorkState, String> {
    /**
     * 現在、ある作業が実行されているか確認
     *
     * @param wid
     * @return boolean
     */
    public boolean existsByWid(String wid);

    /**
     * 情報を公開してよい全てユーザの作業状況を所得
     *
     * @param discloseflag
     * @return List<WorkState>
     */
    public List<WorkState> findByDiscloseflag(boolean discloseflag);

    /**
     * 情報を公開してよいすべてのユーザのうち、作業中のユーザを所得
     *
     * @param discloseflag
     * @param wid
     * @return List<WorkState>
     */
    public List<WorkState> findByDiscloseflagAndWidNotAndUidNot(boolean discloseflag, String wid, String uid);
}
