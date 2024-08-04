import { animate,style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Input, input, OnInit, Output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReadSong } from '../../service/model/song.model';

@Component({
  selector: 'app-song-card',
  standalone: true,
  imports: [FontAwesomeModule],
  templateUrl: './song-card.component.html',
  styleUrl: './song-card.component.scss',
  animations: [
    trigger(
      'inOutAnimation',
      [
        transition(
          ':enter',
          [
            style({transform: 'translateY(10px)', opacity: 0}),
            animate('.2s ease-out',
              style({transform: 'translateY(0px)', opacity: 1}))
          ]
        ),
        transition(
          ':leave',
          [
            style({transform: 'translateY(0px)', opacity: 1}),
            animate('.2s ease-in',
              style({transform: 'translateY(10px)', opacity: 0}))
          ]
        ),
      ]
    )
  ]
})
export class SongCardComponent implements OnInit {
  @Input() song: ReadSong | undefined;
  songDisplay: ReadSong = {favorite: false, displayPlay: false};

  @Output() songToPlay$ = new EventEmitter<ReadSong>();

    ngOnInit(): void {
      if (this.song) {
        this.songDisplay = { ...this.song, displayPlay: false };
      }
    }

    onHoverPlay(displayIcon: boolean): void {
      this.songDisplay.displayPlay = displayIcon;
    }

    play(): void {
      this.songToPlay$.next(this.songDisplay);
    }
}
