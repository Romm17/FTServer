import { UrlParamsHelper, UrlParam } from '../url-params.helper';
export class PaymentFilters {
    public sum?: boolean;
    public group?: number;
    public from?: number;
    public to?: number;
    public user?: number;
    public page? = 0;
    public size? = 10;
    constructor(init?: Partial<PaymentFilters>) {
        Object.assign(this, init);
    }
    public toUrl(): string {
        const helper = new UrlParamsHelper();
        if (!this.sum) {
            helper.add('page', this.page);
            helper.add('size', this.size);
        }
        if (!!this.from && !this.sum) {
            helper.add('userFrom', this.from);
        }
        if (!!this.to && !this.sum) {
            helper.add('userTo', this.to);
        }
        if (this.group > -1) {
            helper.add('group', this.group);
        }
        if (!!this.sum && !!this.user) {
            helper.add('user', this.user);
        }
        if (!!this.sum) {
            return '/sum' + helper.toString();
        }
        return helper.toString();
    }
}
