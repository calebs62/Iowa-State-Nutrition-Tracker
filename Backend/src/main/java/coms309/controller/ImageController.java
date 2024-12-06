package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.*;
import org.springframework.web.bind.annotation.RestController;

import coms309.entity.*;
import coms309.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@RestController
@ServerEndpoint(value="/img")
public class ImageController {
    @Autowired
    ImageGalleryRepository imgRepo;
    @Autowired
    ActivityFeedRepository afRepo;

    @GetMapping("/{id}")
    public ImageGallery getById(@PathVariable int id) {
        return imgRepo.findById(id).orElse(null);
    }

    @PostMapping("{id}")
    public ImageGallery uploadImage(@RequestBody String imgText) {
        ImageGallery img = new ImageGallery(imgText);
        imgRepo.save(img);
        return img;
    }

    @PutMapping("{id}")
    public ImageGallery updateImage(@PathVariable int id, @RequestBody String imgText) {
        ImageGallery img = imgRepo.findById(id).orElse(null);
        if (img != null && imgText != null && !imgText.isEmpty()) {
            img.setImg(imgText);
        }
        return img;
    }
    @DeleteMapping("/update/{id}")
    public ImageGallery deleteImage(@PathVariable int id) {
        ImageGallery img = imgRepo.findById(id).orElse(null);
        if (img != null) {
            imgRepo.delete(img);
        }
        return img;
    }

    @PutMapping("{imgid}/{activityid}")
    public ActivityFeed addImageToActivity(@PathVariable int imgid, @PathVariable int activityid) {
        ImageGallery img = imgRepo.findById(imgid).orElse(null);
        ActivityFeed af = afRepo.findById(activityid).orElse(null);
        if (img == null || af == null) {
            return null;
        }
        else {
            af.getImages().add(img);
            return af;
        }

    }

}
