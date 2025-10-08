package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ShopType> queryList() {
        String key = "login:type:";
        List<String> shopTypeList = redisTemplate.opsForList().range(CACHE_SHOP_TYPE, 0, -1);
        if ( shopTypeList != null && !shopTypeList.isEmpty()) {
            List<ShopType> typeList = shopTypeList.stream()
                    .map(shopType -> JSONUtil.toBean(shopType, ShopType.class))
                    .collect(Collectors.toList());
            return typeList;
        }
        List<ShopType> typeList = this.query().orderByAsc("sort").list();
        if ( typeList == null || typeList.isEmpty()) {
            return typeList;
        }
        for ( ShopType shopType : typeList ) {
            redisTemplate.opsForList().rightPush(CACHE_SHOP_TYPE, JSONUtil.toJsonStr(shopType));
        }
        return typeList;
    }
}
