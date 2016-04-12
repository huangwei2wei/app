// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public class ObstacleAvoidanceDebugData
{
    private int _nsamples;
    private int _maxSamples;
    private float[] _vel;
    private float[] _ssize;
    private float[] _pen;
    private float[] _vpen;
    private float[] _vcpen;
    private float[] _spen;
    private float[] _tpen;
    
    public ObstacleAvoidanceDebugData() {
        this._nsamples = 0;
        this._maxSamples = 0;
        this._vel = null;
        this._ssize = null;
        this._pen = null;
        this._vpen = null;
        this._vcpen = null;
        this._spen = null;
        this._tpen = null;
    }
    
    public Boolean Init(final int maxSamples) {
        try {
            if (maxSamples <= 0) {
                throw new Exception("Max Samples must be larger than 0");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this._maxSamples = maxSamples;
        this._vel = new float[maxSamples * 3];
        this._pen = new float[maxSamples];
        this._ssize = new float[maxSamples];
        this._vpen = new float[maxSamples];
        this._vcpen = new float[maxSamples];
        this._spen = new float[maxSamples];
        this._tpen = new float[maxSamples];
        return true;
    }
    
    public void Reset() {
        this._nsamples = 0;
    }
    
    public void AddSample(final float[] vel, final float ssize, final float pen, final float vpen, final float vcpen, final float spen, final float tpen) {
        if (this._nsamples >= this._maxSamples) {
            return;
        }
        System.arraycopy(this._vel, this._nsamples * 3, vel, 0, 3);
        this._ssize[this._nsamples] = ssize;
        this._pen[this._nsamples] = pen;
        this._vpen[this._nsamples] = vpen;
        this._vcpen[this._nsamples] = vcpen;
        this._spen[this._nsamples] = spen;
        this._tpen[this._nsamples] = tpen;
        ++this._nsamples;
    }
    
    public void NormalizeSamples() {
        this._pen = this.NormalizeArray(this._pen, this._nsamples);
        this._vpen = this.NormalizeArray(this._vpen, this._nsamples);
        this._vcpen = this.NormalizeArray(this._vcpen, this._nsamples);
        this._spen = this.NormalizeArray(this._spen, this._nsamples);
        this._tpen = this.NormalizeArray(this._tpen, this._nsamples);
    }
    
    private float[] NormalizeArray(final float[] arr, final int n) {
        float minPen = Float.MAX_VALUE;
        float maxPen = -3.4028235E38f;
        for (int i = 0; i < n; ++i) {
            minPen = Math.min(minPen, arr[i]);
            maxPen = Math.max(maxPen, arr[i]);
        }
        final float penRange = maxPen - minPen;
        final float s = (penRange > 0.001f) ? (1.0f / penRange) : 1.0f;
        for (int j = 0; j < n; ++j) {
            arr[j] = Math.max(0.0f, Math.min(1.0f, (arr[j] - minPen) * s));
        }
        return arr;
    }
    
    public int SampleCount() {
        return this._nsamples;
    }
    
    public float[] SampleVelocity(final int i) {
        final float[] ret = new float[3];
        System.arraycopy(this._vel, i * 3, ret, 0, 3);
        return ret;
    }
    
    public float SampleSize(final int i) {
        return this._ssize[i];
    }
    
    public float SamplePenalty(final int i) {
        return this._pen[i];
    }
    
    public float SampleDesiredVelocityPenalty(final int i) {
        return this._vpen[i];
    }
    
    public float SampleCurrentVelocityPenalty(final int i) {
        return this._vcpen[i];
    }
    
    public float SamplePreferredSidePenalty(final int i) {
        return this._spen[i];
    }
    
    public float SampleCollisionTimePenalty(final int i) {
        return this._tpen[i];
    }
}
