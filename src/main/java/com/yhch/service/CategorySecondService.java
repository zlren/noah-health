package com.yhch.service;

import com.yhch.pojo.CategoryFirst;
import com.yhch.pojo.CategorySecond;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CategoryFirstService categoryFirstService;

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


    /**
     * 返回属于化验或者医技的亚类组成的id集合
     *
     * @param firstType 化验或医技
     * @return
     */
    public Set<Integer> getSecondIdSetByFirstType(String firstType) {

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setType(firstType);
        List<CategoryFirst> categoryFirstList = this.categoryFirstService.queryListByWhere(categoryFirst);

        Set<Integer> firstSet = new HashSet<>();
        categoryFirstList.forEach(first -> firstSet.add(first.getId()));


        Example example = new Example(CategorySecond.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("firstId", firstSet);
        List<CategorySecond> categorySecondList = this.getMapper().selectByExample(example);

        Set<Integer> secondSet = new HashSet<>();
        categorySecondList.forEach(second -> secondSet.add(second.getId()));

        return secondSet;
    }

}
