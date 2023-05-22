package android.bignerdranch.acgimgs;

public class Imgs_source0 {

    private boolean success;
    private String imgurl;
    private info info;

    public String getImgurl() {
        return imgurl;
    }
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public boolean getSuccess(){
        return success;
    };
    public void setSuccess(boolean success) {
        this.success = success;
    }


    public static class info{
        private int width;
        private int height;
        private String type;

        public int getWidth() {
            return width;
        }
        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }
        public void setHeight(int height) {
            this.height = height;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "info{" +
                    "width=" + width +
                    ", height=" + height +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Imgs_source0{" +
                "success=" + success +
                ", imgurl='" + imgurl + '\'' +
                ", info=" + info +
                '}';
    }
}
