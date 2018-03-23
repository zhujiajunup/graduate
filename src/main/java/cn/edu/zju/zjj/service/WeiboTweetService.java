package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.dao.WeiboTweetDao;
import cn.edu.zju.zjj.entity.Chain;
import cn.edu.zju.zjj.entity.Count;
import cn.edu.zju.zjj.entity.WeiboTweet;

import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: zjj
 * <p>
 * Data: 2017/12/9 18:57
 */
@Service
@Slf4j
public class WeiboTweetService extends BaseService<WeiboTweet> {

    private WeiboTweetDao tweetDao;

    @Autowired
    private WeiboTweetService(WeiboTweetDao tweetDao) {
        super(tweetDao);
        this.tweetDao = tweetDao;
    }

    public List<WeiboTweet> getByUid(String uid) {
        return this.tweetDao.getByUid(uid);
    }

    public int getTotal(String uid) {
        return this.tweetDao.getTotal(uid);
    }

    public List<WeiboTweet> getByPage(String uid, int pageSize, int pageNum) {
        int offset = (pageNum - 1) * pageSize;
        return this.tweetDao.getByPage(uid, pageSize, offset);
    }

    public Map<String, Object> getChain(String tid) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> categoriesIndexMap = new HashMap<>();
        Map<String, Integer> nodesIndexMap = new HashMap<>();
        Map<String, String> linksMap = new HashMap<>();
        Map<String, Integer> cntMap = new HashMap<>();
        List<Chain> chains = this.tweetDao.getChain(tid);
        Stack<String> todo = new Stack<>();
        chains.forEach(
                chain -> todo.add(chain.getToTid())
        );
        while (!todo.isEmpty()) {
            List<Chain> curr = this.tweetDao.getChain(todo.pop());
            if (curr != null && curr.size() > 0) {
                chains.addAll(curr);

                curr.forEach(
                        chain -> todo.add(chain.getToTid())
                );
            }
        }

        List<Map<String, String>> categories = new ArrayList<>();

        List<Node> nodes = new ArrayList<>();

        List<Link> links = new ArrayList<>();

        chains.forEach(
                chain -> {
                    if (!categoriesIndexMap.containsKey(chain.getFrom())) {
                        categoriesIndexMap.put(chain.getFrom(), categories.size());
                        categories.add(new HashMap<String, String>() {
                            {
                                put("name", chain.getFrom());
                            }
                        });
                    }

                    if (!categoriesIndexMap.containsKey(chain.getTo())) {
                        categoriesIndexMap.put(chain.getTo(), categories.size());
                        categories.add(new HashMap<String, String>() {
                            {
                                put("name", chain.getTo());
                            }
                        });
                    }
                    if (!nodesIndexMap.containsKey(chain.getFrom())) {
                        nodesIndexMap.put(chain.getFrom(), nodes.size());
                        nodes.add(new Node(chain.getFrom(), 0, 0));
                    }
                    if (!nodesIndexMap.containsKey(chain.getTo())) {
                        nodesIndexMap.put(chain.getTo(), nodes.size());
                        nodes.add(new Node(chain.getTo(), 0, 0));
                    }
                    int cnt = cntMap.getOrDefault(chain.getFrom(), 0);
                    cnt += 1;
                    cntMap.put(chain.getFrom(), cnt);
                    cnt = cntMap.getOrDefault(chain.getTo(), 0);
                    cnt += 1;
                    cntMap.put(chain.getTo(), cnt);
                }
        );
        chains.forEach(
                chain -> {
                    int source = nodesIndexMap.get(chain.getFrom());
                    int target = nodesIndexMap.get(chain.getTo());
                    links.add(new Link(source, target));
                    linksMap.put(chain.getTo(), chain.getFrom());
                }
        );
        nodes.forEach(
                node -> {

                    node.setCategory(categoriesIndexMap.get(linksMap.get(node.getName())));
                    node.setValue(cntMap.get(node.getName()));
                }
        );

        log.info("{}", chains);
        result.put("categories", categories);
        result.put("nodes", nodes);
        result.put("links", links);
        result.put("type", "force");
        return result;
    }


    public ChainNode getChain2(String tid) {
        Stack<String> tids = new Stack<>();
        tids.push(tid);
        Map<String, ChainNode> nodeMap = new HashMap<>();
        while (!tids.isEmpty()) {
            String id = tids.pop();
            List<Chain> curr = this.tweetDao.getChain(id);
            if (curr != null && curr.size() > 0) {
                curr.forEach(
                        chain -> {
                            if (chain.getToTid() != null) {
                                if (!chain.getFromTid().equals(chain.getToTid())) {
                                    ChainNode fromNode = nodeMap.getOrDefault(chain.getFromTid(),
                                            ChainNode.builder().children(new ArrayList<>()).name(chain.getFrom()).build());
                                    ChainNode toNode = nodeMap.getOrDefault(chain.getToTid(),
                                            ChainNode.builder().children(new ArrayList<>()).name(chain.getTo()).build());

                                    fromNode.getChildren().add(toNode);
                                    nodeMap.put(chain.getFromTid(), fromNode);
                                    nodeMap.put(chain.getToTid(), toNode);

                                    tids.push(chain.getToTid());
                                }
                            }else{
                                ChainNode node = nodeMap.get(chain.getFromTid());
                                node.setValue("1");
                                node.setChildren(null);
                            }
                        }
                );
            }
        }

        return nodeMap.get(tid);
    }

    public List<WeiboTweet> getRepost(String tid) {
        return this.tweetDao.getRepost(tid);
    }
    public List<Count> statTime(String tweetId) {
        return this.tweetDao.statTime(tweetId);
    }


    @Setter
    @Getter
    @ToString(of = "name")
    @EqualsAndHashCode(of = "name")
    @Builder
    private static class ChainNode {
        String name;
        List<ChainNode> children;
        String value;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = "name")
    @AllArgsConstructor
    private static class Node {
        String name;
        int value;
        int category;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"source", "target"})
    private static class Link {
        int source;
        int target;
    }

}

