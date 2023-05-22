package android.bignerdranch.acgimgs;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface Img_Interface {

    @GET
    Call<List<Imgs_source1>> getImgs(@Url String url);

    @GET
    Call<Imgs_source0> getImg(@Url String url);
}
