package map.develop.com.mapdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class FullScreenActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fullscreen);
    ImageView iv = findViewById(R.id.photo);
    String url = getIntent().getStringExtra("Photo");
    Glide.with(this)
        .load(url)
        .into(iv);

  }
}
