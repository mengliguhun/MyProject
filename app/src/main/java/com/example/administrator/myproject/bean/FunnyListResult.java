package com.example.administrator.myproject.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/1/6.
 */
public class FunnyListResult {

    private int count;
    private int err;
    private int total;
    private int page;
    private int refresh;
    /**
     * high_url:""
     * format : image
     * image : app114567738.jpg
     * published_at : 1452032102
     * tag :
     * user : {"avatar_updated_at":1449615711,"last_visited_at":1449615710,"created_at":1449615710,"state":"active","last_device":"android_8.4.0","role":"n","login":".最美","id":30751828,"icon":"20151208230151.jpg"}
     * image_size : {"s":[183,352,11867],"m":[415,800,85609]}
     * id : 114567738
     * votes : {"down":-225,"up":2095}
     * created_at : 1452015904
     * content : 人家就单纯的问一下精确时间啊
     * state : publish
     * comments_count : 51
     * allow_comment : true
     * share_count : 282
     * type : hot
     */

    private List<ItemsEntity> items;

    public void setCount(int count) {
        this.count = count;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRefresh(int refresh) {
        this.refresh = refresh;
    }

    public void setItems(List<ItemsEntity> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public int getErr() {
        return err;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getRefresh() {
        return refresh;
    }

    public List<ItemsEntity> getItems() {
        return items;
    }

    public static class ItemsEntity {
        private boolean isVisible;
        private boolean isPause;
        private String format;
        private String image;
        private int published_at;
        private String tag;
        /**
         * avatar_updated_at : 1449615711
         * last_visited_at : 1449615710
         * created_at : 1449615710
         * state : active
         * last_device : android_8.4.0
         * role : n
         * login : .最美
         * id : 30751828
         * icon : 20151208230151.jpg
         */

        private UserEntity user;
        private ImageSizeEntity image_size;
        private int id;
        /**
         * down : -225
         * up : 2095
         */

        private VotesEntity votes;
        private int created_at;
        private String content;
        private String state;
        private int comments_count;
        private boolean allow_comment;
        private int share_count;
        private String type;
        /**
         * pic_size : [480,480]
         * pic_url : http://qiubai-video.qiushibaike.com/EJNXR3GRUV7QH3MJ.jpg
         * low_url : http://qiubai-video.qiushibaike.com/EJNXR3GRUV7QH3MJ_3g.mp4
         */
        private String high_url;
        private String pic_url;
        private String low_url;
        private List<Integer> pic_size;

        public String getHigh_url() {
            return high_url;
        }

        public void setHigh_url(String high_url) {
            this.high_url = high_url;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setPublished_at(int published_at) {
            this.published_at = published_at;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public void setImage_size(ImageSizeEntity image_size) {
            this.image_size = image_size;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setVotes(VotesEntity votes) {
            this.votes = votes;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setComments_count(int comments_count) {
            this.comments_count = comments_count;
        }

        public void setAllow_comment(boolean allow_comment) {
            this.allow_comment = allow_comment;
        }

        public void setShare_count(int share_count) {
            this.share_count = share_count;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFormat() {
            return format;
        }

        public String getImage() {
            return image;
        }

        public int getPublished_at() {
            return published_at;
        }

        public String getTag() {
            return tag;
        }

        public UserEntity getUser() {
            return user;
        }

        public ImageSizeEntity getImage_size() {
            return image_size;
        }

        public int getId() {
            return id;
        }

        public VotesEntity getVotes() {
            return votes;
        }

        public int getCreated_at() {
            return created_at;
        }

        public String getContent() {
            return content;
        }

        public String getState() {
            return state;
        }

        public int getComments_count() {
            return comments_count;
        }

        public boolean isAllow_comment() {
            return allow_comment;
        }

        public int getShare_count() {
            return share_count;
        }

        public String getType() {
            return type;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public void setLow_url(String low_url) {
            this.low_url = low_url;
        }

        public void setPic_size(List<Integer> pic_size) {
            this.pic_size = pic_size;
        }

        public String getPic_url() {
            return pic_url;
        }

        public String getLow_url() {
            return low_url;
        }

        public List<Integer> getPic_size() {
            return pic_size;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setIsVisible(boolean isVisible) {
            this.isVisible = isVisible;
        }

        public void setPause(boolean pause) {
            isPause = pause;
        }

        public boolean isPause() {
            return isPause;
        }

        public static class UserEntity {
            private int avatar_updated_at;
            private int last_visited_at;
            private int created_at;
            private String state;
            private String last_device;
            private String role;
            private String login;
            private int id;
            private String icon;

            public void setAvatar_updated_at(int avatar_updated_at) {
                this.avatar_updated_at = avatar_updated_at;
            }

            public void setLast_visited_at(int last_visited_at) {
                this.last_visited_at = last_visited_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }

            public void setState(String state) {
                this.state = state;
            }

            public void setLast_device(String last_device) {
                this.last_device = last_device;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public void setLogin(String login) {
                this.login = login;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getAvatar_updated_at() {
                return avatar_updated_at;
            }

            public int getLast_visited_at() {
                return last_visited_at;
            }

            public int getCreated_at() {
                return created_at;
            }

            public String getState() {
                return state;
            }

            public String getLast_device() {
                return last_device;
            }

            public String getRole() {
                return role;
            }

            public String getLogin() {
                return login;
            }

            public int getId() {
                return id;
            }

            public String getIcon() {
                return icon;
            }
        }

        public static class ImageSizeEntity {
            private List<Integer> s;
            private List<Integer> m;

            public void setS(List<Integer> s) {
                this.s = s;
            }

            public void setM(List<Integer> m) {
                this.m = m;
            }

            public List<Integer> getS() {
                return s;
            }

            public List<Integer> getM() {
                return m;
            }
        }

        public static class VotesEntity {
            private int down;
            private int up;

            public void setDown(int down) {
                this.down = down;
            }

            public void setUp(int up) {
                this.up = up;
            }

            public int getDown() {
                return down;
            }

            public int getUp() {
                return up;
            }
        }
    }
}
