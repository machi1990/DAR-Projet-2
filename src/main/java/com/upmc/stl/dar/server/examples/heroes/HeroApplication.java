package com.upmc.stl.dar.server.examples.heroes;

import java.util.ArrayList;
import java.util.Collections;

import com.upmc.stl.dar.server.annotation.CONSUMES;
import com.upmc.stl.dar.server.annotation.GET;
import com.upmc.stl.dar.server.annotation.PARAM;
import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.annotation.POST;
import com.upmc.stl.dar.server.annotation.PRODUCES;
import com.upmc.stl.dar.server.annotation.CONSUMES.Consumed;
import com.upmc.stl.dar.server.request.ContentType;

@PATH("/heroes")
public class HeroApplication {

	private static ArrayList<Hero> heroes = new ArrayList<>();

	static {
		initializeDB();
	}

	public static class Hero implements Comparable<Hero>{
		public Integer id = -1;
		public String name;
		public String details;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Hero other = (Hero) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public int compareTo(Hero hero) {
			return id.compareTo(hero.id);
		}

	}

	private static void initializeDB() {
		Hero hero = new Hero();
		hero.id = 0;
		hero.name = "Simeone";
		hero.details = "There is a pig box for you. Better start learning your english bwaaay.";
		heroes.add(hero);
		
		hero = new Hero();
		hero.id = 1;
		hero.name = "Henry";
		hero.details = "Vava vooooooom.";
		heroes.add(hero);

		hero = new Hero();
		hero.id = 2;
		hero.name = "Toure";
		hero.details = "Yaaayaaa Yaaayaa Tooouree. Kolo Kolo Kolo Touuuuureee.";
		heroes.add(hero);
		
		hero = new Hero();
		
		hero.id = 3;
		hero.name = "Ozil";
		hero.details = "I do not know who you are, but I'll find you and give you and assist.";
		heroes.add(hero);

		hero = new Hero();
		
		hero.id = 4;
		hero.name = "Cazorla";
		hero.details = "Oooh Santi Cathooorlaaa Ooooh Santi Cazorlaaaaa.";
		heroes.add(hero);
		
		hero = new Hero();

		hero.id = 5;
		hero.name = "Welbeck";
		hero.details = "Who\'s dat guy? Dat Guy Welbz. He\'s dat guy.";
		heroes.add(hero);
		
		hero = new Hero();
		
		hero.id = 6;
		hero.name = "Giroud";
		hero.details = "Nanananana na na na ana naa na Girouooooood. Naaaa na na naaaaa naaaa na na anaaa Giroud.";
		heroes.add(hero);

		hero = new Hero();
		hero.id = 7;
		hero.name = "Pogba";
		hero.details = "Pog boooooom. Dab BOSS! Pol Pol Poob Boooooooom.";
		heroes.add(hero);

		hero = new Hero();
		hero.id = 8;
		hero.name = "Lukaku";
		hero.details = "Bolingoooooo Dab Daddy loook at my Dab. Dab is the new word in the dictionary. Ooooh Look at ma Dab.";
		heroes.add(hero);
	}

	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/list")
	public ArrayList<Hero> list() {
		Collections.sort(heroes);
		return new ArrayList<>(heroes);
	}

	@POST
	@PRODUCES(ContentType.JSON)
	@CONSUMES(Consumed.JSON)
	@PATH("/add")
	public Hero add(Hero hero) {
		hero.id = heroes.size();
		heroes.add(hero);
		return hero;
	}

	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/retrieve/<id>")
	public Hero retrieve(@PARAM("<id>") Integer id) {
		Hero hero = new Hero();
		for (Hero hero_ : heroes) {
			if (hero_.id.equals(id)) {
				return hero_;
			}
		}
		return hero;
	}

	@POST
	@PRODUCES(ContentType.JSON)
	@CONSUMES(Consumed.JSON)
	@PATH("/update")
	public Hero update(Hero hero) {
		heroes.remove(hero);
		heroes.add(hero);
		return hero;
	}

	@GET
	@PRODUCES(ContentType.JSON)
	@PATH("/delete/<id>")
	public boolean delete(@PARAM("<id>") Integer id) {
		Hero toDelete = new Hero();

		for (Hero hero_ : heroes) {
			if (hero_.id.equals(id)) {
				toDelete = hero_;
				break;
			}
		}

		Boolean res = heroes.remove(toDelete);

		if (res) {
			for (Hero hero_ : heroes) {
				if (hero_.id > id) {
					hero_.id--;
				}
			}
		}

		return res;
	}
}
