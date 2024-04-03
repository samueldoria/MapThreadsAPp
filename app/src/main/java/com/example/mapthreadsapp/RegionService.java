package com.example.mapthreadsapp;

import android.view.View;

import java.util.List;

public class RegionService {
    private CallbackToast callbackToast;
    private RegionQueue regionQueue;
    private RegionRepository regionRepository;

    public RegionService(CallbackToast callbackToast) {
        this.callbackToast = callbackToast;
        this.regionQueue = new RegionQueue();
        this.regionRepository = new RegionRepository();
    }

    public Iterable<Region> getAllRegions() {
        List<Region> regions = (List<Region>) regionRepository.getRegions(callbackToast);
        List<Region> regionsQueue = (List<Region>) regionQueue.getRegions();
        regions.addAll(regionsQueue);

        return regions;
    }

    public void addRegion(Region region) {
        Boolean checkRepository = regionRepository.checkNewRegion(region, callbackToast);
        if(!checkRepository) return;
        regionQueue.add(region, callbackToast);
    }

    public void saveDatabase() {

        if(regionQueue.isEmpty()) {
            callbackToast.onComplete("Nenhuma região para salvar");
            return;
        }
        new Thread(() -> {
            // Roda enquanto a fila nao estiver vazia
            while (!regionQueue.isEmpty()) {
                Region region = regionQueue.remove();
                regionRepository.add(region, callbackToast);
            };
        }).start();
    }

}
