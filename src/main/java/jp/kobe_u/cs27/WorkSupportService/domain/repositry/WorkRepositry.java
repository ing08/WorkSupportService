package jp.kobe_u.cs27.WorkSupportService.domain.repositry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;

/**
 * 作業のリポジトリ
 *
 * @author ing
 */
@Repository
public interface WorkRepositry extends CrudRepository<Work, String> {
    /**
     * 作業名で作業を所得
     *
     * @param wname
     * @return Work
     */
    public Work findByWname(String wname);
}