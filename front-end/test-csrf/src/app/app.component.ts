import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, tap} from "rxjs";
import {CookieService} from "ngx-cookie-service";

interface Bond {
  isin: string
  name: string
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'test-csrf';
  result?: Observable<Bond>

  constructor(private http: HttpClient, private cookieService: CookieService) {

  }

  fetchData() {
    const token = "andyHello";
    console.log(document.cookie)
    this.cookieService.set("token", token)
    this.result = this.http.get<Bond>(`http://localhost:8080/bondlic/bond/popular?token=${token}`, {
      headers: {
        token
      },
      withCredentials: true
    })
      .pipe(
        tap(result => {
          console.log(result)
        })
      )
  }
}
