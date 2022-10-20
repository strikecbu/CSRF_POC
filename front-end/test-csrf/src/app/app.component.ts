import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {concatMap, from, generate, map, Observable, reduce, tap} from "rxjs";
import {CookieService} from "ngx-cookie-service";
import {FormControl, FormGroup, Validators} from "@angular/forms";

interface Bond {
  isin: string
  name: string
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'test-csrf';
  result?: Observable<Bond>
  result_popular?: Observable<Bond>
  formG: FormGroup = new FormGroup({
    keyWords: new FormControl("haha", Validators.required),
    type: new FormControl(null),
  });
  formGWithFile: FormGroup = new FormGroup({
    keyWords: new FormControl("haha", Validators.required),
    type: new FormControl(null),
    file: new FormControl(null)
  });

  file?: File


  constructor(private httpClient: HttpClient, private cookieService: CookieService) {

  }

  fetchPopularData() {
    const token = "andyHello";
    console.log(document.cookie)
    this.cookieService.set("token", token)
    this.result_popular = this.httpClient.get<Bond>(`http://localhost:8080/bondlic/bond/popular?token=${token}`, {
      headers: {
        token,
        "Api-Key": "BBBBBBB"
      },
      withCredentials: true
    })
      .pipe(
        tap(result => {
          console.log(result)
        })
      )
  }

  fetchSearchData() {
    const token = "andyHello";
    console.log('he', this.formG.getRawValue())
    console.log(document.cookie)
    this.cookieService.set("token", token)

    this.result = this.httpClient.post<Bond>(`http://localhost:8080/bondlic/bond/search`,
      {
        ...this.formG.getRawValue(),
        keyWords: [this.formG.getRawValue().keyWords]
      },
      {
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


  uploadData() {
    const token = "andyHello";
    console.log('he', this.formGWithFile.getRawValue())
    console.log(document.cookie)
    this.cookieService.set("token", token)
    console.log('a:', this.formGWithFile)

    const formData = new FormData();

    // Store form name as "file" with file data
    console.log("size:", this.file!.size)
    formData.append("file", this.file!.slice(), this.file?.name);
    formData.append("docName", '12333')
    const a: Bond = {
      isin: '123',
      name: 'cola'
    }
    formData.append("json", JSON.stringify(a))

    this.httpClient.post(`http://localhost:8087/bondlic/bond/upload`, formData)
      .subscribe(res => {
        console.log(res)
      })
    // this.http.post<Bond>(`http://localhost:8080/bondlic/bond/search`,
    //   {
    //     ...this.formG.getRawValue(),
    //     keyWords: [this.formG.getRawValue().keyWords]
    //   },
    //   {
    //     headers: {
    //       token
    //     },
    //     withCredentials: true
    //   })
  }

  onChange(event: any) {
    this.file = event.target.files[0];
    from(event.target.files as File[])
      .pipe(
        concatMap(this.splitUpload())
      )
      .subscribe(res => {
        console.log('final file name: ', res)
      })
  }

  private splitUpload() {
    return (file: File) => {
      // 10000000 = 10Mb
      const uploadSize = 1000000;
      const fileSize = file.size;
      const randomFileName = this._uuid();
      return generate({
        initialState: 0,
        condition: x => x < fileSize,
        iterate: x => x + uploadSize,
      })
        .pipe(
          tap(pos => {
            console.log('file position: ', pos)
          }),
          concatMap(position => {
            const formData = new FormData();
            let endOfPosition = position + uploadSize;
            if (position + uploadSize > fileSize) {
              endOfPosition = fileSize
            }

            formData.append("file", file.slice(position, endOfPosition), randomFileName);
            Math.random().toFixed(10)
            formData.append("position", position + '')
            return this.httpClient.post("http://localhost:8087/bondlic/bond/upload", formData)
              .pipe(
                map((res: any) => {
                  return res.fileName;
                })
              )
          }),
          reduce((acc, curr) => {
            return [...acc, curr];
          }, [] as string[]),
          concatMap(fileNames => {
            return this.httpClient.post("http://localhost:8087/bondlic/bond/combine", {
              names: fileNames
            }).pipe(
              map((res: any) => {
                return res.fileName;
              })
            )
          })
        )
    };
  }

  private _uuid() {
    function s4() {
      return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
  }


}
