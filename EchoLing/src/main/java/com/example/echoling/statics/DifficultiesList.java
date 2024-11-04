package com.example.echoling.statics;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Scope("prototype")
public class DifficultiesList {

    public List<Difficulty> getDifficulties() {
        return Arrays.asList(
                new Difficulty("Essential", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgSEfFB9rs5JfmP1XHu4oqeBLXfqEtaeH2ECOfAZvWDgAPxTWUKwdXUf3-TmYJeQM4oU00l1LSdukZgL7riCWyafDYNIv2vZ1Rp6Xkkp7QqbTY7uEMtLpyNv1Lh5c7d4nKbBcfwPW9yxw8/s1600/praying-emoji.png"),
                new Difficulty("Beginner", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiIWy2YibZsI0lA7OSRSNq-VI0LlADU6eBXTKIcOwp-ZDXn8_24sEMCz5bz5WbAGS66wyJePxk3GmgQsK9-UQalgLWhsFgEGgY6l3bVIQ34tAOW2XkATGuJRMPXjWYOKycbnTbxFTd_gwo/s1600/sad-face-emoji.png"),
                new Difficulty("Intermediate", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhL_eJgfzrzXo-rdvoFvS23RGTErx6J1rM0BGJWXaYbVRGadQmfyXCptOFaY0lSDoxvuiCayxxOy1XmoNdjlkSDjRTET20Ba14A47hkiek8KS07hX4uA7JRevy8kAeUrqIR_pGS0DuRX6g/s1600/shrug-smiley.png"),
                new Difficulty("Advanced", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEicUYPEKoIEUmxq1aTyzYtG0xEjpb55ELYFBQ2aDSNLN23xs4jzT0aBFccHkbfS-rRA4trGwyl07fafkysv1znYecQUaHMIY0FdpQmAz-DuVztyWTjEUv1tFoiD0Fhfm0-OejWH6qKDCOc/s1600/shiny-smile-emoji.png"),
                new Difficulty("Professional", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiH29xLgZmKklY8jmLvyIo053FpbaymY9gyZWkRfGGxELwk_cf_1wY29xRZcHc3s4scPel5LY3R8eCAGxN-ruTJhlHJ2k3mEiHqSeZH_SinSB0LIHS8ts6JukgvXh6afiJZYnd8Yzu-YPM/s1600/rose-in-mouth-emoji.png")
        );
    }
}