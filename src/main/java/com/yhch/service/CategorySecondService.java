package com.yhch.service;

import com.yhch.pojo.CategorySecond;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zlren on 2017/6/12.
 */
@Service
public class CategorySecondService extends BaseService<CategorySecond> {

    /**
     * 根据亚类名称模糊匹配，将匹配的结果的id组成set返回
     *
     * @param secondName
     * @return
     */
    public Set<Integer> getIdSetBySecondNameLike(String secondName) {

        Example example = new Example(CategorySecond.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("name", "%" + secondName + "%");
        List<CategorySecond> categorySecondList = this.getMapper().selectByExample(example);

        Set<Integer> secondIdSet = new HashSet<>();
        categorySecondList.forEach(second -> secondIdSet.add(second.getId()));
        return secondIdSet;
    }

}
