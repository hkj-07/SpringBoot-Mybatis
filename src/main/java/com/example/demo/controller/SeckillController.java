package com.example.demo.controller;

import com.example.demo.Utils.JsonUtil;
import com.example.demo.bean.Order;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/seckill")
@Slf4j
public class SeckillController {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 数据预热到redis后的实现,且使用lua脚本解决用户重复秒杀的问题,并引入kafka异步处理订单
     *
     * @param pid
     * @param uid
     * @return
     */
    @GetMapping("/{pid}/{uid}")
    public String seckillWithLua(@PathVariable long pid, @PathVariable long uid) throws Exception {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // 设置脚本返回类型
        redisScript.setResultType(Long.class);
        //加载脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("seckill.lua")));
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(pid + ""), uid + "");
        log.info("result:{}", result);

        if (result == 1) {
            //创建订单
            Order order = new Order();
            order.setPid(pid);
            order.setUid(uid);
            //将创建订单的消息放入kafka消息队列
            kafkaTemplate.send("seckill", JsonUtil.objectToJson(order));
        }
        return result > 0 ? "秒杀成功" : "秒杀失败";
    }

    //kafka消费者
    @KafkaListener(topics = "seckill", groupId = "g1")
    public void consumer(ConsumerRecord<?, String> record) {
        String value = record.value();
        //字符串转对象
//        Order order = JsonUtil.json2Obj(value, Order.class);
//        orderService.insert(order);
        return;
    }

//    /**
//     * 数据预热到redis后的实现,且使用lua脚本解决用户重复秒杀的问题
//     * @param pid
//     * @param uid
//     * @return
//     */
//    @GetMapping("/{pid}/{uid}")
//    public String seckillWithLua1(@PathVariable long pid, @PathVariable long uid) {
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//        redisScript.setResultType(Long.class);
//        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("seckill.lua")));
//        Long result = redisTemplate.execute(redisScript, Collections.singletonList(pid + ""), uid + "");
//        log.info("result:{}", result);
//
//        if (result == 1) {
//            //创建订单
//            Order order = new Order();
//            order.setPid(pid);
//            order.setUid(uid);
//            orderService.insert(order);
//        }
//        return result > 0 ? "秒杀成功" : "秒杀失败";
//    }

//    /**
//     * 数据预热到redis后的实现（添加秒杀结束标志）
//     *
//     * @param pid 商品id
//     * @param uid 用户id
//     * @return
//     */
//    @GetMapping("/{pid}/{uid}")
//    public String seckillWithRedis(@PathVariable long pid, @PathVariable long uid) {
//        //先判断库存量是否还有，不然则直接结束
//        String end = redisTemplate.opsForValue().get("seckill:" + pid + ":end");
//        if (Objects.equals(end, "1")) {
//            return "秒杀结束";
//        }
//        //返回的decrement为剩余的库存
//        Long decrement = redisTemplate.opsForValue().decrement("seckill:" + pid + ":stock");
//        //剩余库存大于等于0都为秒杀成功
//        if (decrement >= 0) {
//            if (decrement == 0) {
//                //设置该商品秒杀状态为结束
//                redisTemplate.opsForValue().set("seckill:" + pid + ":end", "1");
//            }
//            //创建订单
//            Order order = new Order();
//            order.setPid(pid);
//            order.setUid(uid);
//            orderService.insert(order);
//        }
//        log.info("decrement:{}", decrement);
//        return decrement < 0 ? "秒杀失败" : "秒杀成功";
//    }


//    /**
//     * 数据预热到redis后的实现
//     *
//     * @param pid 商品id
//     * @param uid 用户id
//     * @return
//     */
//    @GetMapping("/{pid}/{uid}")
//    public String seckillWithRedis(@PathVariable long pid, @PathVariable long uid) {
//        //返回的decrement为剩余的库存
//        Long decrement = redisTemplate.opsForValue().decrement("seckill:" + pid+":stock");
//        //剩余库存大于等于0都为秒杀成功
//        if (decrement >= 0) {
//            //创建订单
//            Order order = new Order();
//            order.setPid(pid);
//            order.setUid(uid);
//            orderService.insert(order);
//        }
//        log.info("decrement:{}", decrement);
//        return decrement < 0 ? "秒杀失败" : "秒杀成功";
//    }


//    /**
//     * 未引入redis和kafka版实现
//     *
//     * @param pid 商品id
//     * @param uid 用户id
//     * @return
//     */
//    @Transactional
//    @GetMapping("/{pid}/{uid}")
//    public String seckill(@PathVariable long pid, @PathVariable long uid) {
//        //1.从数据库获取商品的库存量
//        Product product = productService.selectById(pid);
//        int stock = product.getStock();
//        //2.判断库存是否足够,足够则进入秒杀
//        if (stock > 0) {
//            //执行秒杀逻辑
//            int result = productService.seckill(pid);
//            if (result == 1) {
//                //创建订单
//                Order order = new Order();
//                order.setPid(pid);
//                order.setUid(uid);
//                orderService.insert(order);
//            }
//            return result > 0 ? "秒杀成功" : "秒杀失败";
//        }
//        return "秒杀失败";
//    }
}
