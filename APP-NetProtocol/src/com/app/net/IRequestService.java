package com.app.net;
public abstract interface IRequestService extends IService {
    public abstract void add(int paramInt, IRequest paramIRequest);

    public abstract IRequest remove(int paramInt);
}
