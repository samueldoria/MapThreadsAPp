package com.example.mapthreadsapp;

import android.location.Location;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class RegionQueue {
    private Queue<Region> regionsQueue;
    private Semaphore semaphore;

    public RegionQueue() {
        regionsQueue = new LinkedList<>();
        semaphore = new Semaphore(1, true);

    }

    public Iterable<Region> getRegions() {
        return regionsQueue;
    }

    public Region remove() {
        return regionsQueue.remove();
    }

    public Boolean isEmpty() {
        return regionsQueue.isEmpty();
    }

    public void add(Region newRegion, CallbackToast callbackToast) {
        Thread thread = new Thread(() -> {
            try {
                semaphore.acquire();
                for (Region region : regionsQueue) {
                    if (region.isNear(newRegion)) {
                        callbackToast.onComplete("Região à menos de 30 metros da " + region.getName());
                        semaphore.release();
                        return;
                    }
                }
                regionsQueue.add(newRegion);
                callbackToast.onComplete("Região adicionada com sucesso");
                semaphore.release();
            } catch (InterruptedException e) {
                callbackToast.onComplete("Erro ao adicionar região");
                e.printStackTrace();
                semaphore.release();
            }
        });
        thread.start();
    }
}
